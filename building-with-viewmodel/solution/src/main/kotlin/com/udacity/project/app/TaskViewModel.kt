package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel

// Step 1: Extend ViewModel class to manage UI-related data in a lifecycle-conscious way
class TaskViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 4 // Start at 4 since we initialize with 3 sample tasks

    init {
        // Step 2: Initialize with sample data so the app has data when it starts
        _tasks.add(Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false))
        _tasks.add(Task(id = 2, title = "Review MVVM architecture patterns", completed = true))
        _tasks.add(Task(id = 3, title = "Build task manager app", completed = false))

        // Log ViewModel hashCode to demonstrate it survives configuration changes
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // Method to get the current list of tasks
    fun getTasks(): List<Task> {
        return _tasks.toList()
    }

    // Step 3: Business logic method to add a new task
    fun addTask(title: String) {
        // Create a new task with the next available ID
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )

        // Add the new task to the list
        _tasks.add(newTask)
    }

    // Step 3: Business logic method to toggle task completion status
    fun toggleTaskCompletion(taskId: Int) {
        // Find the task by ID and toggle its completion status
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = _tasks[index]
            _tasks[index] = task.copy(completed = !task.completed)
        }
    }

    // Step 3: Business logic method to delete a task
    fun deleteTask(taskId: Int) {
        // Remove the task with the specified ID
        _tasks.removeIf { it.id == taskId }
    }

    // Method to get the total number of tasks
    fun getTotalTaskCount(): Int {
        return _tasks.size
    }

    // Method to get the number of completed tasks
    fun getCompletedTaskCount(): Int {
        return _tasks.count { it.completed }
    }
}