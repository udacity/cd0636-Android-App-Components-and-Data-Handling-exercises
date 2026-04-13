package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TaskViewModel with Retrofit integration for network operations.
 * Demonstrates API calls using coroutines and proper error handling.
 */
class TaskViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 4

    // Sync state management with StateFlow
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

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
    // Retrofit API Integration
    // ========================================

    /**
     * Syncs tasks from JSONPlaceholder API using Retrofit.
     *
     * This function demonstrates:
     * - viewModelScope for automatic lifecycle management
     * - Dispatchers.IO for network operations
     * - Retrofit suspend function calls
     * - DTO to domain model mapping
     * - Error handling with try-catch
     * - StateFlow for reactive state updates
     */
    fun syncTasks() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                // Network call on IO dispatcher
                val taskDtos = withContext(Dispatchers.IO) {
                    TasksService.api.getTasks()
                }

                // Map DTOs to domain Task objects
                // Take only first 20 tasks to keep list manageable
                val tasks = taskDtos.take(20).map { dto ->
                    Task(
                        id = dto.id,
                        title = dto.title,
                        completed = dto.completed
                    )
                }

                // Update task list
                _tasks.clear()
                _tasks.addAll(tasks)

                // Update next task ID to avoid conflicts
                nextTaskId = tasks.maxOfOrNull { it.id }?.plus(1) ?: 1

                _syncState.value = SyncState.Success
                Log.d("TaskViewModel", "Synced ${tasks.size} tasks from API")
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
                Log.e("TaskViewModel", "Sync failed", e)
            }
        }
    }

    /**
     * Fetches a single task by ID from the API.
     * Demonstrates parameterized API calls with @Path.
     */
    fun fetchTaskById(taskId: Int) {
        viewModelScope.launch {
            try {
                val taskDto = withContext(Dispatchers.IO) {
                    TasksService.api.getTask(taskId)
                }

                val task = Task(
                    id = taskDto.id,
                    title = taskDto.title,
                    completed = taskDto.completed
                )

                // Add or update task in list
                val index = _tasks.indexOfFirst { it.id == taskId }
                if (index != -1) {
                    _tasks[index] = task
                } else {
                    _tasks.add(task)
                }

                Log.d("TaskViewModel", "Fetched task: ${task.title}")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to fetch task $taskId", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}

/**
 * Sealed class representing sync operation states.
 */
sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}