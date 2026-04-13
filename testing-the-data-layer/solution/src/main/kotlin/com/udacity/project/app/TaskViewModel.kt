package com.udacity.project.app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task list with dependency injection.
 * Repository is injected via constructor for testability.
 */
class TaskViewModel(
    private val repository: TaskRepository,
    @Suppress("UNUSED_PARAMETER")
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalTaskCount: StateFlow<Int> = tasks.map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val completedTaskCount: StateFlow<Int> = tasks.map { list -> list.count { it.completed } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )


    fun addTask(title: String) {
        viewModelScope.launch {
            val task = Task(
                id = System.currentTimeMillis().toInt(),
                title = title.trim(),
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

    fun toggleTaskFavorite(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = tasks.value
            val task = currentTasks.find { it.id == taskId }
            task?.let {
                repository.updateTask(it.copy(isFavorite = !it.isFavorite))
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
