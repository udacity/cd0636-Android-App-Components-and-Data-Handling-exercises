package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

// TODO: Step 2 - Update constructor to accept SavedStateHandle
// Add SavedStateHandle as a constructor parameter:
// class TaskViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
// Import required: import androidx.lifecycle.SavedStateHandle
class TaskViewModel : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        Task(id = 3, title = "Build task manager app", completed = false)
    )

    // TODO: Step 3 - Replace MutableLiveData with SavedStateHandle.getLiveData()
    // Replace the two lines below with:
    // val tasks: LiveData<List<Task>> = savedStateHandle.getLiveData("tasks", defaultTasks)
    // This will automatically persist changes across process death
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    val totalTaskCount: LiveData<Int> = _tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = _tasks.map { taskList ->
        taskList.count { it.completed }
    }

    init {
        _tasks.value = defaultTasks
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // TODO: Step 4 - Update addTask to use SavedStateHandle
    // Replace _tasks.value = updatedTasks with:
    // savedStateHandle["tasks"] = updatedTasks
    fun addTask(title: String) {
        val currentTasks = _tasks.value ?: emptyList()
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        val updatedTasks = currentTasks + newTask
        _tasks.value = updatedTasks
    }

    // TODO: Step 4 - Update toggleTaskCompletion to use SavedStateHandle
    // Replace _tasks.value = updatedTasks with:
    // savedStateHandle["tasks"] = updatedTasks
    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = _tasks.value ?: emptyList()
        val updatedTasks = currentTasks.map { task ->
            if (task.id == taskId) {
                task.copy(completed = !task.completed)
            } else {
                task
            }
        }
        _tasks.value = updatedTasks
    }

    // TODO: Step 4 - Update deleteTask to use SavedStateHandle
    // Replace _tasks.value = updatedTasks with:
    // savedStateHandle["tasks"] = updatedTasks
    fun deleteTask(taskId: Int) {
        val currentTasks = _tasks.value ?: emptyList()
        val updatedTasks = currentTasks.filter { it.id != taskId }
        _tasks.value = updatedTasks
    }
}