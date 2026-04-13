package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

/**
 * ViewModel with form validation for task management.
 */
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

    // Validation state - observe this to display errors in UI
    private val _taskTitleValidation = MutableLiveData<ValidationResult>(ValidationResult.Valid)
    val taskTitleValidation: LiveData<ValidationResult> = _taskTitleValidation

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // TODO: Step 1 - Implement validateTaskTitle method
    // Define MIN_TITLE_LENGTH and MAX_TITLE_LENGTH constants
    // Check for empty, too short, and too long titles
    // Return ValidationResult.Valid or ValidationResult.Invalid with message
    // fun validateTaskTitle(title: String): ValidationResult {
    //     return when {
    //         title.isBlank() -> ValidationResult.Invalid("Task title cannot be empty")
    //         // Add length checks here
    //         else -> ValidationResult.Valid
    //     }
    // }

    // TODO: Step 2 - Update addTask to validate before saving
    // Call validateTaskTitle(title) first
    // Only save if validation passes
    // Update _taskTitleValidation to show errors
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

    // Optional: Replace above with version that accepts callbacks
    // fun addTask(title: String, onSuccess: () -> Unit, onError: () -> Unit)

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

    companion object {
        private const val MIN_TITLE_LENGTH = 3
        private const val MAX_TITLE_LENGTH = 100
    }
}
