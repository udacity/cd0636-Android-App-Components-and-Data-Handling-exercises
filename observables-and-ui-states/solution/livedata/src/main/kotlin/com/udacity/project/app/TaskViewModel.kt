package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class TaskViewModel : ViewModel() {

    // Private mutable LiveData for internal updates
    private val _tasks = MutableLiveData<List<Task>>()

    // Public immutable LiveData for external observation
    val tasks: LiveData<List<Task>> get() = _tasks

    private var nextTaskId = 4

    init {
        // Initialize LiveData with sample data
        _tasks.value = listOf(
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

    // Computed LiveData properties that automatically recalculate when tasks change
    val totalTaskCount: LiveData<Int> = _tasks.map { it.size }
    val completedTaskCount: LiveData<Int> = _tasks.map { it.count { task -> task.completed } }

    // Generic helper function to update the task list
    private fun updateTaskList(transform: (List<Task>) -> List<Task>) {
        val updatedTasks = transform(_tasks.value ?: emptyList())
        _tasks.value = updatedTasks
    }
}