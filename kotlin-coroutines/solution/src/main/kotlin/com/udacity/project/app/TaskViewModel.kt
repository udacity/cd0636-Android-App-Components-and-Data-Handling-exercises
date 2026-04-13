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
 * Demonstrates viewModelScope, dispatchers, async/await, and cancellation.
 */
class TaskViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 4 // Start at 4 since we initialize with 3 sample tasks

    // Sync state management
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Progress tracking
    private val _syncProgress = MutableStateFlow(0)
    val syncProgress: StateFlow<Int> = _syncProgress.asStateFlow()

    // Task statistics from server
    private val _syncStats = MutableStateFlow<TaskStats?>(null)
    val syncStats: StateFlow<TaskStats?> = _syncStats.asStateFlow()

    // Job reference for cancellation
    private var syncJob: Job? = null

    init {
        // Initialize with sample data
        _tasks.add(Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false))
        _tasks.add(Task(id = 2, title = "Review MVVM architecture patterns", completed = true))
        _tasks.add(Task(id = 3, title = "Build task manager app", completed = false))

        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // ========================================
    // Basic Task Operations
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
    // Coroutine-Based Async Operations
    // ========================================

    /**
     * TODO 1.1: Sync tasks from server using viewModelScope.
     * Demonstrates basic coroutine usage with viewModelScope and Dispatchers.IO.
     */
    fun syncTasks() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                // Use IO dispatcher for network operations
                val syncedTasks = withContext(Dispatchers.IO) {
                    // Simulate network call
                    delay(2000)
                    fetchTasksFromServer()
                }

                // Update tasks with synced data
                _tasks.clear()
                _tasks.addAll(syncedTasks)

                // Back to Main dispatcher (automatic) for UI update
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }

    /**
     * TODO 1.3: Sync tasks and stats in parallel using async/await.
     * Demonstrates structured concurrency with parallel operations.
     */
    fun syncTasksAndStats() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                // Run both operations in parallel using async
                val tasksDeferred = async(Dispatchers.IO) {
                    delay(2000)
                    fetchTasksFromServer()
                }

                val statsDeferred = async(Dispatchers.IO) {
                    delay(1500)
                    fetchStatsFromServer()
                }

                // Wait for both to complete
                val tasks = tasksDeferred.await()
                val stats = statsDeferred.await()

                // Update data
                _tasks.clear()
                _tasks.addAll(tasks)
                _syncStats.value = stats

                _syncState.value = SyncState.Success
            } catch (e: CancellationException) {
                _syncState.value = SyncState.Cancelled
                throw e // Re-throw to properly cancel
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }

    /**
     * TODO 2.1: Sync with progress updates using StateFlow.
     * Demonstrates progress tracking for long-running operations.
     */
    fun syncTasksWithProgress() {
        syncJob = viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _syncProgress.value = 0

            try {
                withContext(Dispatchers.IO) {
                    // Step 1: Fetch tasks (25%)
                    _syncProgress.value = 25
                    delay(500)
                    val tasks = fetchTasksFromServer()

                    // Check if cancelled
                    ensureActive()

                    // Step 2: Process tasks (50%)
                    _syncProgress.value = 50
                    delay(500)
                    processTasks(tasks)

                    // Check if cancelled
                    ensureActive()

                    // Step 3: Save tasks (75%)
                    _syncProgress.value = 75
                    delay(500)
                    _tasks.clear()
                    _tasks.addAll(tasks)

                    // Check if cancelled
                    ensureActive()

                    // Step 4: Complete (100%)
                    _syncProgress.value = 100
                    delay(300)
                }

                _syncState.value = SyncState.Success
            } catch (e: CancellationException) {
                _syncState.value = SyncState.Cancelled
                _syncProgress.value = 0
                throw e // Re-throw to properly cancel
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
                _syncProgress.value = 0
            }
        }
    }

    /**
     * TODO 2.2: Cancel ongoing sync operation.
     * Demonstrates cancellation handling with proper cleanup.
     */
    fun cancelSync() {
        syncJob?.cancel()
        _syncState.value = SyncState.Cancelled
        _syncProgress.value = 0
    }

    /**
     * TODO 1.2: Export tasks using IO dispatcher.
     * Demonstrates file I/O operations with proper dispatcher.
     */
    fun exportTasks(): String {
        // Return message about export (simplified version without actual file I/O)
        // In a real app, this would use Dispatchers.IO for file writing
        val taskCount = _tasks.size
        return "Exported $taskCount tasks successfully"
    }

    // ========================================
    // Mock Server Functions
    // ========================================

    /**
     * Simulates fetching tasks from a remote server.
     */
    private suspend fun fetchTasksFromServer(): List<Task> {
        // Simulate network delay
        delay(1000)

        // Return mock data
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
        // Simulate network delay
        delay(800)

        // Calculate mock stats
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
        // Simulate processing time
        delay(500)
        // In a real app, this would validate, transform, or enrich task data
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel any ongoing operations when ViewModel is cleared
        syncJob?.cancel()
        Log.d("TaskViewModel", "ViewModel cleared - cancelling operations")
    }
}