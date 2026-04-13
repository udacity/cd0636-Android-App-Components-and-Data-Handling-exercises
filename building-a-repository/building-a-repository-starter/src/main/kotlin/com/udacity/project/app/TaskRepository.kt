package com.udacity.project.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Repository that serves as the single source of truth for Task data.
 *
 * Responsibilities:
 * - Coordinate between API (remote) and SavedStateHandle (local cache)
 * - Provide LiveData backed by cache for UI observation
 * - Implement cache-first loading strategy
 * - Handle network errors gracefully
 * - Implement smart refresh logic to minimize network calls
 *
 * Architecture:
 * TaskViewModel -> TaskRepository -> [API + Cache]
 *
 * @param apiService API service for network requests
 * @param savedStateHandle Local cache that survives configuration changes
 * @param dispatcher Coroutine dispatcher for IO operations (injected for testability)
 */
class TaskRepository(
    private val apiService: TaskApiService,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val TAG = "TaskRepository"

        // TODO 1.1: Define cache constants
        // Uncomment and complete these constants:
        // private const val TASKS_KEY = "tasks"
        // private const val LAST_FETCH_KEY = "last_fetch_time"
        // private const val CACHE_TIMEOUT = 5 * 60 * 1000L // 5 minutes
    }

    /**
     * TODO 1.2: Implement getTasks()
     *
     * Return LiveData backed by SavedStateHandle (cache).
     * This is the single source of truth that the UI observes.
     *
     * Steps:
     * 1. Use savedStateHandle.getLiveData() with TASKS_KEY
     * 2. Provide emptyList() as default value
     * 3. Return LiveData<List<Task>>
     */

    /**
     * TODO 1.3: Implement refreshTasks()
     *
     * Fetch tasks from API and update cache.
     * Implements cache-first strategy where cache is updated immediately.
     *
     * Steps:
     * 1. Wrap everything in try-catch
     * 2. Fetch data from network using withContext(dispatcher) { apiService.getTasks() }
     * 3. Transform DTOs to domain models using .map { it.toDomainModel() }
     * 4. Take only first 20 tasks with .take(20)
     * 5. Update cache: savedStateHandle[TASKS_KEY] = tasks
     * 6. Update timestamp: savedStateHandle[LAST_FETCH_KEY] = System.currentTimeMillis()
     * 7. Return Result.success(Unit)
     * 8. Catch exceptions and return Result.failure(exception)
     *
     * Note: Use the injected dispatcher, not Dispatchers.IO directly
     */

    /**
     * TODO 1.4: Implement shouldRefresh()
     *
     * Check if the cache is stale and needs to be refreshed.
     * Cache is considered stale if it's older than CACHE_TIMEOUT (5 minutes).
     *
     * Steps:
     * 1. Get last fetch time: savedStateHandle.get<Long>(LAST_FETCH_KEY) ?: 0
     * 2. Get current time: System.currentTimeMillis()
     * 3. Calculate age: now - lastFetch
     * 4. Return true if age > CACHE_TIMEOUT
     */

    /**
     * TODO 1.5: Implement syncIfNeeded()
     *
     * Smart sync that only refreshes if the cache is stale.
     * This reduces unnecessary network calls and data usage.
     *
     * Steps:
     * 1. Check if refresh is needed with shouldRefresh()
     * 2. If true, call refreshTasks() and return its result
     * 3. If false, return Result.success(Unit) immediately
     */

    /**
     * TODO 1.6 (Optional): Implement CRUD operations
     *
     * Add methods to manipulate tasks in the cache:
     *
     * fun addTask(task: Task) {
     *     val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
     *     val updatedTasks = currentTasks + task
     *     savedStateHandle[TASKS_KEY] = updatedTasks
     * }
     *
     * fun updateTask(task: Task) {
     *     val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
     *     val updatedTasks = currentTasks.map { if (it.id == task.id) task else it }
     *     savedStateHandle[TASKS_KEY] = updatedTasks
     * }
     *
     * fun deleteTask(taskId: Int) {
     *     val currentTasks = savedStateHandle.get<List<Task>>(TASKS_KEY) ?: emptyList()
     *     val updatedTasks = currentTasks.filter { it.id != taskId }
     *     savedStateHandle[TASKS_KEY] = updatedTasks
     * }
     *
     * Note: These update the cache (SavedStateHandle) which automatically
     * notifies all LiveData observers.
     */
}