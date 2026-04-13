package com.udacity.project.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task list with Room database.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            val task = Task(
                id = System.currentTimeMillis().toInt(),
                title = title,
                completed = false,
                isFavorite = false
            )
            repository.addTask(task)
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = tasks.value
            val task = currentTasks.find { it.id == taskId }
            task?.let {
                repository.updateTask(it.copy(completed = !it.completed))
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}