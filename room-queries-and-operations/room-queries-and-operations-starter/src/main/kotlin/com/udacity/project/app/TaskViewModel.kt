package com.udacity.project.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for managing task list with Room database.
 *
 * Complete Part 2 TODOs after implementing Part 1 (TaskDao and TaskRepository).
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    /**
     * TODO 2.1: Implement addTask()
     *
     * Create function that:
     * 1. Launches viewModelScope coroutine
     * 2. Creates Task with:
     *    - id: System.currentTimeMillis().toInt()
     *    - title: from parameter
     *    - completed: false
     * 3. Calls repository.addTask(task)
     */
    // fun addTask(title: String)

    /**
     * TODO 2.2: Implement toggleTaskCompletion()
     *
     * Create function that:
     * 1. Launches viewModelScope coroutine
     * 2. Finds task from tasks.value by taskId
     * 3. Creates updated task with task.copy(completed = !task.completed)
     * 4. Calls repository.updateTask(updatedTask)
     */
    // fun toggleTaskCompletion(taskId: Int)

    /**
     * TODO 2.3: Implement deleteTask()
     *
     * Create function that:
     * 1. Launches viewModelScope coroutine
     * 2. Calls repository.deleteTask(taskId)
     */
    // fun deleteTask(taskId: Int)

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}