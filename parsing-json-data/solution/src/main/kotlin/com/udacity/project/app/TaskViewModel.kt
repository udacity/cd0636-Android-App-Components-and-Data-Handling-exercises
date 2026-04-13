package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * TaskViewModel with Retrofit integration for network operations.
 * Demonstrates API calls using coroutines and proper error handling.
 */
class TaskViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 6

    // Sync state management with StateFlow
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Sample creators for varied task attribution
    private val sampleCreators = listOf(
        "Alice Johnson", "Bob Smith", "Carol Davis",
        "David Chen", "Emma Wilson", "You"
    )

    init {
        // Initialize with diverse sample data showing various scenarios
        _tasks.add(
            Task(
                id = 1,
                title = "Review JSON parsing with Moshi",
                completed = false,
                createdBy = "Alice Johnson",
                dueDate = java.util.Date(System.currentTimeMillis() + 86400000 * 2) // Due in 2 days
            )
        )
        _tasks.add(
            Task(
                id = 2,
                title = "Implement Retrofit API integration",
                completed = true,
                createdBy = "Bob Smith",
                dueDate = java.util.Date(System.currentTimeMillis() - 86400000 * 5) // Completed 5 days ago
            )
        )
        _tasks.add(
            Task(
                id = 3,
                title = "Design RecyclerView layouts",
                completed = false,
                createdBy = "Carol Davis",
                dueDate = null // No specific deadline
            )
        )
        _tasks.add(
            Task(
                id = 4,
                title = "Write unit tests for ViewModel",
                completed = false,
                createdBy = "David Chen",
                dueDate = java.util.Date(System.currentTimeMillis() + 86400000 * 14) // Due in 2 weeks
            )
        )
        _tasks.add(
            Task(
                id = 5,
                title = "Update documentation",
                completed = false,
                createdBy = "Emma Wilson",
                dueDate = java.util.Date(System.currentTimeMillis() + 86400000) // Due tomorrow
            )
        )

        Log.d("TaskViewModel", "ViewModel initialized with ${_tasks.size} tasks - hashCode: ${this.hashCode()}")
    }

    // ========================================
    // Basic Task Operations
    // ========================================

    fun getTasks(): List<Task> {
        return _tasks.toList()
    }

    fun addTask(title: String) {
        // Calculate varied due dates: 1-7 days from now, or 30% chance of no due date
        val dueDate = if (Math.random() < 0.3) {
            null // No due date (30% chance)
        } else {
            val daysAhead = (1..7).random()
            java.util.Date(System.currentTimeMillis() + 86400000 * daysAhead)
        }

        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false,
            createdBy = "You", // User-created tasks
            dueDate = dueDate
        )
        _tasks.add(newTask)
        Log.d("TaskViewModel", "Added task: $title (due: ${dueDate?.let { "set" } ?: "none"})")
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
     * Syncs tasks from JSONPlaceholder API using Retrofit with complex JSON parsing.
     *
     * This function demonstrates:
     * - viewModelScope for automatic lifecycle management
     * - Dispatchers.IO for network operations
     * - Retrofit suspend function calls with Moshi parsing
     * - TaskResponse to domain model mapping with toDomainModel()
     * - Specific error handling for JSON parsing errors
     * - StateFlow for reactive state updates
     */
    fun syncTasks() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                // Network call on IO dispatcher
                val taskResponses = withContext(Dispatchers.IO) {
                    TasksService.api.getTasks()
                }

                // Map API responses to domain Task objects using extension function
                // Take only first 20 tasks to keep list manageable
                val tasks = taskResponses.take(20).map { response ->
                    response.toDomainModel()
                }

                // Update task list
                _tasks.clear()
                _tasks.addAll(tasks)

                // Update next task ID to avoid conflicts
                nextTaskId = tasks.maxOfOrNull { it.id }?.plus(1) ?: 1

                _syncState.value = SyncState.Success
                Log.d("TaskViewModel", "Synced ${tasks.size} tasks from API")
            } catch (e: JsonDataException) {
                // Handle JSON parsing errors specifically
                _syncState.value = SyncState.Error("Invalid data format: ${e.message}")
                Log.e("TaskViewModel", "JSON parsing failed", e)
            } catch (e: IOException) {
                // Handle network errors
                _syncState.value = SyncState.Error("Network error: ${e.message}")
                Log.e("TaskViewModel", "Network error during sync", e)
            } catch (e: Exception) {
                // Handle any other errors
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
                Log.e("TaskViewModel", "Sync failed", e)
            }
        }
    }

    /**
     * Fetches a single task by ID from the API.
     * Demonstrates parameterized API calls with @Path and complex JSON parsing.
     */
    fun fetchTaskById(taskId: Int) {
        viewModelScope.launch {
            try {
                val taskResponse = withContext(Dispatchers.IO) {
                    TasksService.api.getTask(taskId)
                }

                // Convert API response to domain model
                val task = taskResponse.toDomainModel()

                // Add or update task in list
                val index = _tasks.indexOfFirst { it.id == taskId }
                if (index != -1) {
                    _tasks[index] = task
                } else {
                    _tasks.add(task)
                }

                Log.d("TaskViewModel", "Fetched task: ${task.title} (created by: ${task.createdBy})")
            } catch (e: JsonDataException) {
                Log.e("TaskViewModel", "Failed to parse task $taskId", e)
            } catch (e: IOException) {
                Log.e("TaskViewModel", "Network error fetching task $taskId", e)
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