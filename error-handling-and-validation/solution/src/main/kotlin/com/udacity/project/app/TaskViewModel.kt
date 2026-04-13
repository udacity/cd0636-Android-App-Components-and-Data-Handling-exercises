package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel with error handling for network sync operations.
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

    // Sync state management
    private val _syncState = MutableLiveData<NetworkResult<Unit>>()
    val syncState: LiveData<NetworkResult<Unit>> = _syncState

    private var lastSyncAttempt: Long = 0
    private val minSyncInterval = 30_000L // 30 seconds

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    /**
     * Syncs tasks with simulated network operation and error handling.
     */
    fun syncTasks() {
        // Prevent too frequent sync attempts
        val now = System.currentTimeMillis()
        if (now - lastSyncAttempt < minSyncInterval) {
            _syncState.value = NetworkResult.Error(
                NetworkError.Unknown("Please wait before trying again")
            )
            return
        }
        lastSyncAttempt = now

        viewModelScope.launch {
            _syncState.value = NetworkResult.Loading

            // Simulate network delay
            delay(1500)

            // Simulate different error scenarios randomly for demonstration
            val result = simulateNetworkSync()

            _syncState.value = when (result) {
                is NetworkResult.Success -> {
                    Log.d("TaskViewModel", "Sync successful")
                    NetworkResult.Success(Unit)
                }
                is NetworkResult.Error -> {
                    Log.e("TaskViewModel", "Sync failed: ${result.error.message}")
                    result
                }
                NetworkResult.Loading -> NetworkResult.Loading
            }
        }
    }

    /**
     * Simulates network sync with various error scenarios.
     * In production, this would call a real repository method.
     */
    private suspend fun simulateNetworkSync(): NetworkResult<Unit> {
        // Randomly simulate different scenarios for demonstration
        return when (Random.nextInt(6)) {
            0 -> NetworkResult.Error(NetworkError.NoInternet())
            1 -> NetworkResult.Error(NetworkError.Timeout())
            2 -> NetworkResult.Error(NetworkError.ServerError(500))
            3 -> NetworkResult.Error(NetworkError.NotFound())
            4 -> NetworkResult.Error(NetworkError.Unknown("Unexpected error occurred"))
            else -> NetworkResult.Success(Unit)
        }
    }

    /**
     * Checks if enough time has passed to allow another sync attempt.
     */
    fun canRetrySync(): Boolean {
        val now = System.currentTimeMillis()
        return now - lastSyncAttempt >= minSyncInterval
    }

    /**
     * Resets sync state to remove error/success messages.
     */
    fun resetSyncState() {
        _syncState.value = null
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
}