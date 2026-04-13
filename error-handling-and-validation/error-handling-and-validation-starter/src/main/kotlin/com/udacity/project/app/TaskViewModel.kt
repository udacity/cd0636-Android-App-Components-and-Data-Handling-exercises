package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel with error handling for sync operations.
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

    // TODO: Step 1.1 - Add sync state management
    // Create a private MutableLiveData<NetworkResult<Unit>?> named _syncState
    // Expose it as public LiveData<NetworkResult<Unit>?> named syncState
    // Initialize to null

    init {
        Log.d("TaskViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    // TODO: Step 1.2 - Implement syncTasks() function
    // This function should:
    // 1. Launch a coroutine in viewModelScope
    // 2. Set _syncState to NetworkResult.Loading
    // 3. Add a delay(1500) to simulate network latency
    // 4. Call simulateNetworkSync() and store the result
    // 5. Update _syncState with the result
    // 6. Add Log.d statements for debugging

    // TODO: Step 1.3 - Implement simulateNetworkSync() private suspend function
    // This function should return NetworkResult<Unit>
    // Use Random.nextInt(6) to simulate different error scenarios:
    // - 0: NetworkResult.Error(NetworkError.NoInternet())
    // - 1: NetworkResult.Error(NetworkError.Timeout())
    // - 2: NetworkResult.Error(NetworkError.ServerError(500))
    // - 3: NetworkResult.Error(NetworkError.NotFound())
    // - 4: NetworkResult.Error(NetworkError.Unknown("Unexpected error occurred"))
    // - else: NetworkResult.Success(Unit)

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
