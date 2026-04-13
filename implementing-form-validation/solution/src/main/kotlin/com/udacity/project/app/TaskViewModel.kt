package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // Validation state
    private val _taskTitleValidation = MutableLiveData<ValidationResult>(ValidationResult.Valid)
    val taskTitleValidation: LiveData<ValidationResult> = _taskTitleValidation

    private var validateJob: Job? = null

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    /**
     * Validates task title against basic rules.
     */
    fun validateTaskTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Invalid("Task title cannot be empty")
            title.length < MIN_TITLE_LENGTH -> ValidationResult.Invalid(
                "Task title must be at least $MIN_TITLE_LENGTH characters"
            )
            title.length > MAX_TITLE_LENGTH -> ValidationResult.Invalid(
                "Task title must not exceed $MAX_TITLE_LENGTH characters"
            )
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates task title asynchronously with debouncing and duplicate checking.
     */
    fun validateTaskTitleAsync(title: String) {
        // Cancel previous validation job
        validateJob?.cancel()

        // Immediate validation for basic rules
        val basicValidation = validateTaskTitle(title)
        if (basicValidation is ValidationResult.Invalid) {
            _taskTitleValidation.value = basicValidation
            return
        }

        // Async validation for duplicates with debouncing
        validateJob = viewModelScope.launch {
            delay(300) // Debounce user input

            // Check for duplicate titles (case-insensitive)
            val isDuplicate = isTaskTitleDuplicate(title)

            _taskTitleValidation.value = if (isDuplicate) {
                ValidationResult.Invalid("A task with this title already exists")
            } else {
                ValidationResult.Valid
            }
        }
    }

    /**
     * Checks if a task title already exists (case-insensitive).
     */
    private fun isTaskTitleDuplicate(title: String): Boolean {
        val currentTasks = tasks.value ?: emptyList()
        return currentTasks.any { it.title.equals(title, ignoreCase = true) }
    }

    /**
     * Adds a new task with validation.
     */
    fun addTask(title: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val validation = validateTaskTitle(title)

        if (validation is ValidationResult.Invalid) {
            _taskTitleValidation.value = validation
            onError()
            return
        }

        // Check for duplicates before adding
        val isDuplicate = isTaskTitleDuplicate(title)

        if (isDuplicate) {
            _taskTitleValidation.value = ValidationResult.Invalid(
                "A task with this title already exists"
            )
            onError()
        } else {
            val currentTasks = tasks.value ?: emptyList()
            val newTask = Task(
                id = nextTaskId++,
                title = title.trim(),
                completed = false
            )
            val updatedTasks = currentTasks + newTask
            savedStateHandle["tasks"] = updatedTasks
            _taskTitleValidation.value = ValidationResult.Valid
            onSuccess()
        }
    }

    /**
     * Resets validation state to Valid.
     */
    fun resetValidation() {
        _taskTitleValidation.value = ValidationResult.Valid
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

    companion object {
        private const val MIN_TITLE_LENGTH = 3
        private const val MAX_TITLE_LENGTH = 100
    }
}