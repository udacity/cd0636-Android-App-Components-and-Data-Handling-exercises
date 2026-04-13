package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    // TODO 1.1.1: Convert to LiveData
    // Replace the line below with: private val _tasks = MutableLiveData<List<Task>>()
    // Then add: val tasks: LiveData<List<Task>> get() = _tasks

    private var nextTaskId = 4

    init {
        // TODO 1.1.1: Initialize LiveData with sample data
        // Use: _tasks.value = listOf(
        //     Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        //     Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        //     Task(id = 3, title = "Build task manager app", completed = false)
        // )

        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // TODO 1.1.2: Update addTask to work with LiveData
    // Use the helper function: updateTaskList { currentTasks -> currentTasks + newTask }
    fun addTask(title: String) {
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        // TODO: Call updateTaskList { currentTasks -> currentTasks + newTask }
    }

    // TODO 1.1.2: Update toggleTaskCompletion to work with LiveData
    // Use the helper function to map and toggle the task
    fun toggleTaskCompletion(taskId: Int) {
        // TODO: Call updateTaskList { currentTasks ->
        //     currentTasks.map { task ->
        //         if (task.id == taskId) task.copy(completed = !task.completed) else task
        //     }
        // }
    }

    // TODO 1.1.2: Update deleteTask to work with LiveData
    // Use the helper function to filter out the task
    fun deleteTask(taskId: Int) {
        // TODO: Call updateTaskList { currentTasks ->
        //     currentTasks.filter { it.id != taskId }
        // }
    }

    // TODO 1.1.3: Add computed LiveData properties
    // Add these two properties:
    // val totalTaskCount: LiveData<Int> = _tasks.map { it.size }
    // val completedTaskCount: LiveData<Int> = _tasks.map { it.count { task -> task.completed } }

    // TODO 1.1.2: Implement this helper function for LiveData updates
    // private fun updateTaskList(transform: (List<Task>) -> List<Task>) {
    //     val updatedTasks = transform(_tasks.value ?: emptyList())
    //     _tasks.value = updatedTasks
    // }
}