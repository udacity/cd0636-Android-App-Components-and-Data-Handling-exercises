package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TaskViewModel : ViewModel() {

    // Private mutable StateFlow for internal updates
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    // Public immutable StateFlow for external observation
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    private var nextTaskId = 4

    init {
        // Initialize StateFlow with sample data
        _tasksFlow.value = listOf(
            Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
            Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
            Task(id = 3, title = "Build task manager app", completed = false)
        )

        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addTask(title: String) {
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        updateTaskList { currentTasks -> currentTasks + newTask }
    }

    fun toggleTaskCompletion(taskId: Int) {
        updateTaskList { currentTasks ->
            currentTasks.map { task ->
                if (task.id == taskId) task.copy(completed = !task.completed) else task
            }
        }
    }

    fun deleteTask(taskId: Int) {
        updateTaskList { currentTasks ->
            currentTasks.filter { it.id != taskId }
        }
    }

    // Computed StateFlow properties that automatically recalculate when tasks change
    val totalTaskCountFlow: StateFlow<Int> = _tasksFlow.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val completedTaskCountFlow: StateFlow<Int> = _tasksFlow.map { it.count { task -> task.completed } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Generic helper function to update the task list
    private fun updateTaskList(transform: (List<Task>) -> List<Task>) {
        val updatedTasks = transform(_tasksFlow.value)
        _tasksFlow.value = updatedTasks
    }
}