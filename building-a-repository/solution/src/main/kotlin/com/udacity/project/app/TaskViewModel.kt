package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * TaskViewModel with Repository pattern integration.
 *
 * This ViewModel demonstrates:
 * - Separation of concerns: ViewModel doesn't know about data sources (API or cache)
 * - Repository pattern: All data access goes through TaskRepository
 * - LiveData for reactive UI updates
 * - Coroutines for asynchronous operations
 * - State management for sync operations
 *
 * Architecture:
 * MainActivity -> TaskViewModel -> TaskRepository -> [API + Cache]
 */
class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Repository instance - single source of truth for task data
    private val repository = TaskRepository(
        apiService = TasksService.api,
        savedStateHandle = savedStateHandle
    )

    // Expose tasks LiveData from repository (backed by cache)
    // UI observes this to display the task list
    val tasks: LiveData<List<Task>> = repository.getTasks()

    // Derived LiveData for statistics
    val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.count { it.completed }
    }

    // Sync state for showing loading/error/success feedback to user
    private val _syncState = MutableLiveData<SyncState>(SyncState.Idle)
    val syncState: LiveData<SyncState> = _syncState

    init {
        Log.d("TaskViewModel", "ViewModel initialized with Repository - hashCode: ${this.hashCode()}")
    }

    /**
     * Manually sync tasks from API (force refresh).
     * Called when user explicitly triggers refresh (e.g., sync button, pull-to-refresh).
     *
     * This always fetches from network, regardless of cache age.
     * Shows loading state and handles errors.
     */
    fun syncTasks() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            Log.d("TaskViewModel", "Manual sync triggered")

            repository.refreshTasks().fold(
                onSuccess = {
                    _syncState.value = SyncState.Success
                    Log.d("TaskViewModel", "Manual sync successful")
                },
                onFailure = { exception ->
                    _syncState.value = SyncState.Error(
                        exception.message ?: "Sync failed"
                    )
                    Log.e("TaskViewModel", "Manual sync failed", exception)
                }
            )
        }
    }

    /**
     * Smart sync that only fetches from network if cache is stale.
     * Called automatically on app start or when returning to the app.
     *
     * This is more efficient than always syncing:
     * - If cache is fresh (< 5 minutes old), uses cached data
     * - If cache is stale (> 5 minutes old), fetches from network
     * - Reduces unnecessary network calls and data usage
     */
    fun syncIfNeeded() {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Smart sync triggered")

            repository.syncIfNeeded().fold(
                onSuccess = {
                    // Don't update sync state for silent background sync
                    // This prevents showing unnecessary loading indicators
                    Log.d("TaskViewModel", "Smart sync completed")
                },
                onFailure = { exception ->
                    // Only log errors from background sync, don't show to user
                    // User still sees cached data
                    Log.e("TaskViewModel", "Smart sync failed (offline?)", exception)
                }
            )
        }
    }

    /**
     * Adds a new task locally.
     * Note: In a production app, this would also send to API and handle sync.
     */
    fun addTask(title: String) {
        val currentTasks = tasks.value ?: emptyList()
        val nextId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1

        val newTask = Task(
            id = nextId,
            title = title,
            completed = false
        )

        repository.addTask(newTask)
        Log.d("TaskViewModel", "Added task: $title")
    }

    /**
     * Toggles task completion status.
     * Note: In a production app, this would also send to API and handle sync.
     */
    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val task = currentTasks.find { it.id == taskId } ?: return

        val updatedTask = task.copy(completed = !task.completed)
        repository.updateTask(updatedTask)
        Log.d("TaskViewModel", "Toggled task completion: $taskId")
    }

    /**
     * Deletes a task.
     * Note: In a production app, this would also send to API and handle sync.
     */
    fun deleteTask(taskId: Int) {
        repository.deleteTask(taskId)
        Log.d("TaskViewModel", "Deleted task: $taskId")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}