package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TaskViewModel with coroutine support for async operations.
 * Complete the TODO steps to add coroutine-based sync functionality.
 */
class TaskViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 4

    // Sync state management (Pre-implemented)
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Progress tracking (Pre-implemented)
    private val _syncProgress = MutableStateFlow(0)
    val syncProgress: StateFlow<Int> = _syncProgress.asStateFlow()

    // Task statistics from server (Pre-implemented)
    private val _syncStats = MutableStateFlow<TaskStats?>(null)
    val syncStats: StateFlow<TaskStats?> = _syncStats.asStateFlow()

    // Job reference for cancellation (Pre-implemented)
    private var syncJob: Job? = null

    init {
        _tasks.add(Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false))
        _tasks.add(Task(id = 2, title = "Review MVVM architecture patterns", completed = true))
        _tasks.add(Task(id = 3, title = "Build task manager app", completed = false))

        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // ========================================
    // Basic Task Operations (Already Implemented)
    // ========================================

    fun getTasks(): List<Task> {
        return _tasks.toList()
    }

    fun addTask(title: String) {
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        _tasks.add(newTask)
    }

    fun toggleTaskCompletion(taskId: Int) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = _tasks[index]
            _tasks[index] = task.copy(completed = !task.completed)
        }
    }

    fun deleteTask(taskId: Int) {
        _tasks.removeIf { it.id == taskId }
    }

    fun getTotalTaskCount(): Int {
        return _tasks.size
    }

    fun getCompletedTaskCount(): Int {
        return _tasks.count { it.completed }
    }

    // ========================================
    // TODO: Implement Coroutine-Based Async Operations
    // ========================================

    /**
     * TODO 1.1: Implement basic sync with viewModelScope
     *
     * Complete this function to sync tasks from the server:
     * 1. Use viewModelScope.launch to start a coroutine
     * 2. Set _syncState to Loading
     * 3. Use withContext(Dispatchers.IO) to switch to IO dispatcher
     * 4. Call fetchTasksFromServer() to get tasks
     * 5. Update _tasks with the synced data
     * 6. Set _syncState to Success
     * 7. Handle exceptions and set _syncState to Error
     *
     * Hint: viewModelScope.launch { ... }
     * Hint: withContext(Dispatchers.IO) { ... }
     * Hint: Use try-catch for error handling
     */
    fun syncTasks() {
        // TODO 1.1: Implement using viewModelScope, Dispatchers.IO, and fetchTasksFromServer()
    }

    /**
     * TODO 1.3: Implement parallel sync with async/await
     *
     * Complete this function to sync tasks and stats in parallel:
     * 1. Use viewModelScope.launch
     * 2. Use async(Dispatchers.IO) for both operations
     * 3. Use await() to get results from both
     * 4. Update _tasks and _syncStats with results
     * 5. Handle CancellationException and other exceptions separately
     *
     * Hint: val tasksDeferred = async(Dispatchers.IO) { ... }
     * Hint: val tasks = tasksDeferred.await()
     * Hint: Catch CancellationException separately and re-throw it
     */
    fun syncTasksAndStats() {
        // TODO 1.3: Implement using async/await for parallel execution
    }

    /**
     * TODO 2.1: Implement sync with progress updates
     *
     * Complete this function to show progress during sync:
     * 1. Store the Job reference in syncJob
     * 2. Update _syncProgress at each step (0, 25, 50, 75, 100)
     * 3. Use ensureActive() to check for cancellation between steps
     * 4. Handle CancellationException separately from other exceptions
     * 5. Reset progress on error or cancellation
     *
     * Hint: syncJob = viewModelScope.launch { ... }
     * Hint: _syncProgress.value = 25
     * Hint: ensureActive() throws CancellationException if cancelled
     */
    fun syncTasksWithProgress() {
        // TODO 2.1: Implement with progress tracking and cancellation support
    }

    /**
     * TODO 2.2: Implement sync cancellation
     *
     * Complete this function to cancel ongoing sync:
     * 1. Call cancel() on the syncJob
     * 2. Update _syncState to Cancelled
     * 3. Reset _syncProgress to 0
     *
     * Hint: syncJob?.cancel()
     */
    fun cancelSync() {
        // TODO 2.2: Implement cancellation
    }

    /**
     * Exports tasks (Pre-implemented - simplified version).
     * In a real app, this would use viewModelScope.launch and Dispatchers.IO.
     */
    fun exportTasks(): String {
        val taskCount = _tasks.size
        return "Exported $taskCount tasks successfully"
    }

    // ========================================
    // Mock Server Functions (Pre-implemented)
    // ========================================

    /**
     * Simulates fetching tasks from a remote server.
     * This represents a network call that takes time.
     */
    private suspend fun fetchTasksFromServer(): List<Task> {
        delay(1000) // Simulate network latency

        return listOf(
            Task(1, "Server Task 1: Update documentation", false),
            Task(2, "Server Task 2: Fix critical bug", true),
            Task(3, "Server Task 3: Deploy to production", false),
            Task(4, "Server Task 4: Code review PR #123", false),
            Task(5, "Server Task 5: Update dependencies", true)
        )
    }

    /**
     * Simulates fetching statistics from a remote server.
     */
    private suspend fun fetchStatsFromServer(): TaskStats {
        delay(800) // Simulate network latency

        val total = _tasks.size
        val completed = _tasks.count { it.completed }
        val pending = total - completed
        val rate = if (total > 0) completed.toFloat() / total else 0f

        return TaskStats(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            completionRate = rate
        )
    }

    /**
     * Simulates processing tasks (validation, transformation, etc.)
     */
    private suspend fun processTasks(tasks: List<Task>) {
        delay(500) // Simulate processing time
    }

    override fun onCleared() {
        super.onCleared()
        // TODO 2.2: Cancel syncJob when ViewModel is destroyed
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}