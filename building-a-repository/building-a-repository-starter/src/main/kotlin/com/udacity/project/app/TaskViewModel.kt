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
 * TaskViewModel that uses Repository pattern for data access.
 *
 * Architecture:
 * MainActivity -> TaskViewModel -> TaskRepository -> [API + Cache]
 *
 * Complete Part 2 TODOs after implementing TaskRepository (Part 1).
 */
class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // TODO 2.1: Create repository instance
    // Uncomment and complete:
    // private val repository = TaskRepository(
    //     apiService = TasksService.api,
    //     savedStateHandle = savedStateHandle
    // )

    // TODO 2.2: Expose tasks from repository
    // Replace direct SavedStateHandle access with repository.getTasks()
    // Uncomment when repository is ready:
    // val tasks: LiveData<List<Task>> = repository.getTasks()

    // Temporary: Direct access until TODO 2.2 is complete
    val tasks: LiveData<List<Task>> = savedStateHandle.getLiveData("tasks", emptyList())

    // Derived LiveData for statistics
    val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.count { it.completed }
    }

    // Sync state for showing loading/error/success feedback
    private val _syncState = MutableLiveData<SyncState>(SyncState.Idle)
    val syncState: LiveData<SyncState> = _syncState

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    /**
     * TODO 2.3: Implement manual sync using repository
     *
     * This method handles user-initiated refresh (e.g., sync button click).
     * Always fetches from network, regardless of cache age.
     *
     * Steps:
     * 1. Set _syncState to Loading
     * 2. Launch coroutine with viewModelScope.launch
     * 3. Call repository.refreshTasks()
     * 4. Use .fold() to handle Result:
     *    - onSuccess: Set _syncState to Success
     *    - onFailure: Set _syncState to Error with exception message
     */
    fun syncTasks() {
        // TODO 2.3: Implement using repository.refreshTasks()
        Log.d("TaskViewModel", "TODO 2.3: syncTasks() not yet implemented")
    }

    /**
     * TODO 2.4: Implement smart sync using repository
     *
     * This method handles automatic background sync (e.g., on app start).
     * Only fetches from network if cache is stale (> 5 minutes old).
     *
     * Steps:
     * 1. Launch coroutine with viewModelScope.launch
     * 2. Call repository.syncIfNeeded()
     * 3. Use .fold() to handle Result:
     *    - onSuccess: Log success (no UI update needed)
     *    - onFailure: Log error (don't show to user, cached data still works)
     *
     * Note: This runs silently in background - no loading state needed
     */
    fun syncIfNeeded() {
        // TODO 2.4: Implement using repository.syncIfNeeded()
        Log.d("TaskViewModel", "TODO 2.4: syncIfNeeded() not yet implemented")
    }

    /**
     * Adds a new task to the list.
     *
     * TODO 2.5 (Optional): Replace direct cache update with repository.addTask()
     * After implementing repository.addTask(), replace the code below with:
     * repository.addTask(newTask)
     */
    fun addTask(title: String) {
        val currentTasks = tasks.value ?: emptyList()
        val nextId = (currentTasks.maxOfOrNull { it.id } ?: 0) + 1

        val newTask = Task(
            id = nextId,
            title = title,
            completed = false
        )

        // Direct cache update (TODO 2.5: Replace with repository.addTask(newTask))
        val updatedTasks = currentTasks + newTask
        savedStateHandle["tasks"] = updatedTasks
        Log.d("TaskViewModel", "Added task: $title")
    }

    /**
     * Toggles task completion status.
     *
     * TODO 2.5 (Optional): Replace direct cache update with repository.updateTask()
     * After implementing repository.updateTask(), replace the code below with:
     * repository.updateTask(updatedTask)
     */
    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val task = currentTasks.find { it.id == taskId } ?: return

        val updatedTask = task.copy(completed = !task.completed)

        // Direct cache update (TODO 2.5: Replace with repository.updateTask(updatedTask))
        val updatedTasks = currentTasks.map {
            if (it.id == taskId) updatedTask else it
        }
        savedStateHandle["tasks"] = updatedTasks
        Log.d("TaskViewModel", "Toggled task completion: $taskId")
    }

    /**
     * Deletes a task from the list.
     *
     * TODO 2.5 (Optional): Replace direct cache update with repository.deleteTask()
     * After implementing repository.deleteTask(), replace the code below with:
     * repository.deleteTask(taskId)
     */
    fun deleteTask(taskId: Int) {
        // Direct cache update (TODO 2.5: Replace with repository.deleteTask(taskId))
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.filter { it.id != taskId }
        savedStateHandle["tasks"] = updatedTasks
        Log.d("TaskViewModel", "Deleted task: $taskId")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}