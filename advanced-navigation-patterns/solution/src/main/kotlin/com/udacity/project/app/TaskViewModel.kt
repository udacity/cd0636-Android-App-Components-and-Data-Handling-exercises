package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        Task(id = 3, title = "Build task manager app", completed = false)
    )

    val tasks: LiveData<List<Task>> = savedStateHandle.getLiveData("tasks", defaultTasks)

    val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.count { it.completed }
    }

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addTask(title: String) {
        val currentTasks = tasks.value ?: emptyList()
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        val updatedTasks = currentTasks + newTask
        savedStateHandle["tasks"] = updatedTasks
    }

    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.map { task ->
            if (task.id == taskId) {
                task.copy(completed = !task.completed)
            } else {
                task
            }
        }
        savedStateHandle["tasks"] = updatedTasks
    }

    fun deleteTask(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.filter { it.id != taskId }
        savedStateHandle["tasks"] = updatedTasks
    }

    fun toggleFavorite(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.map { task ->
            if (task.id == taskId) task.copy(isFavorite = !task.isFavorite)
            else task
        }
        savedStateHandle["tasks"] = updatedTasks
    }
}