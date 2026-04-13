package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel with StateFlow for Compose integration.
 */
class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on Compose", completed = false, isFavorite = true),
        Task(id = 2, title = "Review Material3 components", completed = true, isFavorite = false),
        Task(id = 3, title = "Build task manager with Compose", completed = false, isFavorite = true)
    )

    private val _allTasks = MutableStateFlow(
        savedStateHandle.get<List<Task>>("tasks") ?: defaultTasks
    )
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    val totalTaskCount: StateFlow<Int> = MutableStateFlow(0).apply {
        _allTasks.value.let { value = it.size }
    }

    val completedTaskCount: StateFlow<Int> = MutableStateFlow(0).apply {
        _allTasks.value.let { value = it.count { task -> task.completed } }
    }

    val favoriteTaskCount: StateFlow<Int> = MutableStateFlow(0).apply {
        _allTasks.value.let { value = it.count { task -> task.isFavorite } }
    }

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addTask(title: String) {
        val currentTasks = _allTasks.value
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false,
            isFavorite = false
        )
        val updatedTasks = currentTasks + newTask
        _allTasks.value = updatedTasks
        savedStateHandle["tasks"] = updatedTasks
    }

    fun toggleCompletion(taskId: Int, completed: Boolean) {
        val updatedTasks = _allTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(completed = completed)
            } else {
                task
            }
        }
        _allTasks.value = updatedTasks
        savedStateHandle["tasks"] = updatedTasks
    }

    fun toggleFavorite(taskId: Int, isFavorite: Boolean) {
        val updatedTasks = _allTasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(isFavorite = isFavorite)
            } else {
                task
            }
        }
        _allTasks.value = updatedTasks
        savedStateHandle["tasks"] = updatedTasks
    }

    fun deleteTask(taskId: Int) {
        val updatedTasks = _allTasks.value.filter { it.id != taskId }
        _allTasks.value = updatedTasks
        savedStateHandle["tasks"] = updatedTasks
    }
}