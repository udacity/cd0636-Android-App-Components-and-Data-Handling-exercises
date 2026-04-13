package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * TaskViewModel with Retrofit integration for network operations.
 * Complete the TODOs to fetch tasks from JSONPlaceholder API.
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
    // Basic Task Operations (Pre-implemented)
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
    // TODO 2.1: Retrofit API Integration
    // ========================================

    /**
     * TODO 2.1: Implement Retrofit API call to sync tasks
     *
     * Replace the mock implementation with real Retrofit call:
     * 1. Keep viewModelScope.launch and set _syncState to Loading
     * 2. Use withContext(Dispatchers.IO) for network call
     * 3. Call TasksService.api.getTasks() to get List<TaskDto>
     * 4. Map TaskDto objects to Task objects:
     *    - Take only first 20 tasks: taskDtos.take(20)
     *    - Map each dto to Task(id, title, completed)
     * 5. Clear _tasks and add mapped tasks
     * 6. Update nextTaskId to avoid ID conflicts
     * 7. Set _syncState to Success
     * 8. Handle exceptions with try-catch
     *
     * Hint: val taskDtos = withContext(Dispatchers.IO) { TasksService.api.getTasks() }
     * Hint: val tasks = taskDtos.take(20).map { dto -> Task(...) }
     */
    fun syncTasks() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                // TODO 2.1: Replace with Retrofit API call
                // Mock implementation - currently returns no tasks
                // When you implement Retrofit correctly, tasks will appear
                withContext(Dispatchers.IO) {
                    // Simulate delay
                    delay(1000)
                }

                // Mock: returns empty list until Retrofit is implemented
                _tasks.clear()

                _syncState.value = SyncState.Success
                Log.d("TaskViewModel", "Mock sync completed - 0 tasks (implement Retrofit to load real data)")
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
                Log.e("TaskViewModel", "Sync failed", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}