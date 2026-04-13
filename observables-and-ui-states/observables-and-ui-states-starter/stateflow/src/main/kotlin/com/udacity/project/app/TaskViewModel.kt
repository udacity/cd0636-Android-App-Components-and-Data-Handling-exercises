package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    // TODO 2.1.1: Convert to StateFlow
    // Add: private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    // Then add: val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    private var nextTaskId = 4

    init {
        // TODO 2.1.1: Initialize StateFlow with sample data
        // Use: _tasksFlow.value = listOf(
        //     Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        //     Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        //     Task(id = 3, title = "Build task manager app", completed = false)
        // )

        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // TODO 2.1.2: Update addTask to work with StateFlow
    // Use the helper function: updateTaskList { currentTasks -> currentTasks + newTask }
    fun addTask(title: String) {
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        // TODO: Call updateTaskList { currentTasks -> currentTasks + newTask }
    }

    // TODO 2.1.2: Update toggleTaskCompletion to work with StateFlow
    // Use the helper function to map and toggle the task
    fun toggleTaskCompletion(taskId: Int) {
        // TODO: Call updateTaskList { currentTasks ->
        //     currentTasks.map { task ->
        //         if (task.id == taskId) task.copy(completed = !task.completed) else task
        //     }
        // }
    }

    // TODO 2.1.2: Update deleteTask to work with StateFlow
    // Use the helper function to filter out the task
    fun deleteTask(taskId: Int) {
        // TODO: Call updateTaskList { currentTasks ->
        //     currentTasks.filter { it.id != taskId }
        // }
    }

    // TODO 2.1.3: Add computed StateFlow properties
    // Add these two properties:
    // val totalTaskCountFlow: StateFlow<Int> = _tasksFlow.map { it.size }.stateIn(
    //     scope = viewModelScope,
    //     started = SharingStarted.WhileSubscribed(5000),
    //     initialValue = 0
    // )
    // val completedTaskCountFlow: StateFlow<Int> = _tasksFlow.map { it.count { task -> task.completed } }.stateIn(
    //     scope = viewModelScope,
    //     started = SharingStarted.WhileSubscribed(5000),
    //     initialValue = 0
    // )

    // TODO 2.1.2: Implement this helper function for StateFlow updates
    // private fun updateTaskList(transform: (List<Task>) -> List<Task>) {
    //     val updatedTasks = transform(_tasksFlow.value)
    //     _tasksFlow.value = updatedTasks
    // }
}