package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

/**
 * ViewModel for task management.
 *
 * Complete Part 2 TODOs after implementing Part 1 (TaskAdapter).
 */
class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false, isFavorite = true),
        Task(id = 2, title = "Review MVVM architecture patterns", completed = true, isFavorite = false),
        Task(id = 3, title = "Build task manager app", completed = false, isFavorite = true)
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

    /**
     * TODO 2.1: Implement toggleTaskFavorite()
     *
     * Create function that:
     * 1. Gets current tasks from tasks.value
     * 2. Maps over tasks and toggles isFavorite for matching taskId
     * 3. Updates savedStateHandle with modified list
     */
    // fun toggleTaskFavorite(taskId: Int)

    /**
     * TODO 2.2: Implement addTaskWithDetails()
     *
     * Create function that adds a task with all details (for undo functionality):
     * 1. Gets current tasks from tasks.value
     * 2. Adds the provided task to the list
     * 3. Updates savedStateHandle with modified list
     */
    // fun addTaskWithDetails(task: Task)
}