package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository class that implements the Repository pattern for Task data.
 *
 * This class serves as the single source of truth for task data, coordinating between:
 * - Remote data source (API via TaskApiService)
 * - Local cache (SavedStateHandle)
 *
 * Key responsibilities:
 * - Provide a single LiveData source for UI to observe
 * - Implement cache-first loading strategy
 * - Manage data synchronization from API
 * - Handle network errors gracefully
 * - Implement smart refresh logic to minimize unnecessary network calls
 *
 * Architecture:
 * ViewModel -> Repository -> [API + Cache]
 *                   ↓
 *             LiveData<List<Task>>
 *                   ↓
 *                  UI
 *
 * @param dispatcher Coroutine dispatcher for IO operations (injected for testability)
 */
class TaskRepository(
    private val apiService: TaskApiService,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val TAG = "TaskRepository"

        // SavedStateHandle keys
        private const val TASKS_KEY = "tasks"
        private const val LAST_FETCH_KEY = "last_fetch_time"

        // Cache timeout in milliseconds (5 minutes)
        private const val CACHE_TIMEOUT = 5 * 60 * 1000L
    }

    /**
     * Returns LiveData backed by SavedStateHandle (cache).
     * This is the single source of truth that the UI observes.
     *
     * When cache is updated, all observers are automatically notified.
     * The cache persists across configuration changes and process death.
     */
    fun getTasks(): LiveData<List<Task>> {
        return savedStateHandle.getLiveData(TASKS_KEY, emptyList())
    }

    /**
     * Refreshes tasks from the API and updates the cache.
     * Implements cache-first strategy where cache is updated immediately.
     *
     * Flow:
     * 1. Fetch data from network (on injected dispatcher)
     * 2. Transform DTOs to domain models
     * 3. Update cache (SavedStateHandle)
     * 4. LiveData observers notified automatically
     * 5. Update last fetch timestamp
     *
     * @return Result.success if sync completed, Result.failure if error occurred
     */
    suspend fun refreshTasks(): Result<Unit> {
        return try {
            Log.d(TAG, "Starting task refresh from API")

            // Network call on injected dispatcher (IO by default, can be overridden for testing)
            val taskDtos = withContext(dispatcher) {
                apiService.getTasks()
            }

            // Map API responses to domain Task objects
            // Take only first 20 tasks to keep list manageable
            val tasks = taskDtos.take(20).map { dto ->
                dto.toDomainModel()
            }

            // Update cache (single source of truth)
            savedStateHandle[TASKS_KEY] = tasks
            savedStateHandle[LAST_FETCH_KEY] = System.currentTimeMillis()

            Log.d(TAG, "Successfully synced ${tasks.size} tasks from API")
            Result.success(Unit)

        } catch (e: IOException) {
            // Network error
            Log.e(TAG, "Network error during refresh", e)
            Result.failure(e)
        } catch (e: Exception) {
            // Any other error
            Log.e(TAG, "Unexpected error during refresh", e)
            Result.failure(e)
        }
    }

    /**
     * Checks if the cache is stale and needs to be refreshed.
     * Cache is considered stale if it's older than CACHE_TIMEOUT (5 minutes).
     *
     * @return true if cache should be refreshed, false if cache is fresh
     */
    fun shouldRefresh(): Boolean {
        val lastFetch = savedStateHandle.get<Long>(LAST_FETCH_KEY) ?: 0
        val now = System.currentTimeMillis()
        val cacheAge = now - lastFetch
        val shouldRefresh = cacheAge > CACHE_TIMEOUT

        Log.d(TAG, "Cache age: ${cacheAge / 1000}s, should refresh: $shouldRefresh")
        return shouldRefresh
    }

    /**
     * Smart sync that only refreshes if the cache is stale.
     * This reduces unnecessary network calls and data usage.
     *
     * Use this for automatic background syncs (e.g., on app start).
     * Use refreshTasks() for user-initiated force refresh (e.g., pull-to-refresh).
     *
     * @return Result.success if sync completed or wasn't needed, Result.failure if error occurred
     */
    suspend fun syncIfNeeded(): Result<Unit> {
        return if (shouldRefresh()) {
            Log.d(TAG, "Cache is stale, syncing...")
            refreshTasks()
        } else {
            Log.d(TAG, "Cache is fresh, skipping sync")
            // Cache is fresh, no need to refresh
            Result.success(Unit)
        }
    }

    /**
     * Adds a new task to the cache.
     * Note: This only updates local cache, not the API.
     *
     * For a production app, you would:
     * 1. Send POST request to API
     * 2. Update cache with server response
     * 3. Handle offline scenarios with a sync queue
     */
    fun addTask(task: Task) {
        val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
        val updatedTasks = currentTasks + task
        savedStateHandle[TASKS_KEY] = updatedTasks
        Log.d(TAG, "Added task locally: ${task.title}")
    }

    /**
     * Updates a task in the cache.
     * Note: This only updates local cache, not the API.
     */
    fun updateTask(task: Task) {
        val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
        val updatedTasks = currentTasks.map {
            if (it.id == task.id) task else it
        }
        savedStateHandle[TASKS_KEY] = updatedTasks
        Log.d(TAG, "Updated task locally: ${task.title}")
    }

    /**
     * Deletes a task from the cache.
     * Note: This only updates local cache, not the API.
     */
    fun deleteTask(taskId: Int) {
        val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
        val updatedTasks = currentTasks.filter { it.id != taskId }
        savedStateHandle[TASKS_KEY] = updatedTasks
        Log.d(TAG, "Deleted task locally: $taskId")
    }
}