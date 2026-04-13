package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing task list.
 * Simple in-memory implementation for Room database setup lesson.
 */
class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>(
        listOf(
            Task(id = 1, title = "Setup Room database", completed = false),
            Task(id = 2, title = "Create TaskEntity", completed = true),
            Task(id = 3, title = "Create TaskDao", completed = true),
            Task(id = 4, title = "Create TaskDatabase", completed = false)
        )
    )
    val tasks: LiveData<List<Task>> = _tasks

    private var nextId = 5

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    fun addTask(title: String) {
        val currentTasks = _tasks.value ?: emptyList()
        val newTask = Task(
            id = nextId++,
            title = title,
            completed = false
        )
        _tasks.value = currentTasks + newTask
    }

    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = _tasks.value ?: emptyList()
        _tasks.value = currentTasks.map { task ->
            if (task.id == taskId) {
                task.copy(completed = !task.completed)
            } else {
                task
            }
        }
    }

    fun deleteTask(taskId: Int) {
        val currentTasks = _tasks.value ?: emptyList()
        _tasks.value = currentTasks.filter { it.id != taskId }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}