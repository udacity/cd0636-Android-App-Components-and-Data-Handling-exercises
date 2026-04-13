package com.udacity.project.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>(listOf(
        Task(id = 1, title = "Sample Task 1", completed = false),
        Task(id = 2, title = "Sample Task 2", completed = true)
    ))
    val tasks: LiveData<List<Task>> = _tasks

    private var nextId = 3

    fun addTask(title: String) {
        val currentTasks = _tasks.value ?: emptyList()
        val newTask = Task(id = nextId++, title = title)
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
}