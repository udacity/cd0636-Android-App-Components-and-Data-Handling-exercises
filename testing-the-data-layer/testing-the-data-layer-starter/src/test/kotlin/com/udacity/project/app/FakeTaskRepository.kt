package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of TaskRepository for testing ViewModel.
 * Provides in-memory storage with controllable behavior.
 *
 * TODO 3.1: Create FakeTaskRepository
 * Complete this fake repository to test ViewModel in isolation without real database.
 */
class FakeTaskRepository : TaskRepository {

    // TODO 3.1: Add private storage for tasks
    // Hint: private val tasks = mutableListOf<Task>()
    // Hint: private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    override fun getAllTasks(): Flow<List<Task>> {
        // TODO 3.1: Return the tasksFlow
        TODO("Return tasksFlow")
    }

    override fun getFavoriteTasks(): Flow<List<Task>> {
        // TODO 3.1: Return flow of tasks where isFavorite = true
        // Hint: Use tasksFlow.map { list -> list.filter { it.isFavorite } }
        TODO("Filter and return favorite tasks")
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        // TODO 3.1: Return flow of tasks where completed = false
        // Hint: Use tasksFlow.map { list -> list.filter { !it.completed } }
        TODO("Filter and return active tasks")
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        // TODO 3.1: Find and return task by ID
        // Hint: return tasks.find { it.id == taskId }
        TODO("Find task by ID")
    }

    override suspend fun addTask(task: Task) {
        // TODO 3.1: Add task to storage and update flow
        // Hint: tasks.add(task)
        // Hint: tasksFlow.value = tasks.toList()
        TODO("Add task and update flow")
    }

    override suspend fun updateTask(task: Task) {
        // TODO 3.1: Update existing task in storage
        // Hint: Find task index, replace it, update flow
        TODO("Update task and update flow")
    }

    override suspend fun deleteTask(taskId: Int) {
        // TODO 3.1: Remove task from storage and update flow
        // Hint: tasks.removeIf { it.id == taskId }
        // Hint: tasksFlow.value = tasks.toList()
        TODO("Delete task and update flow")
    }

    override suspend fun isTaskTitleDuplicate(title: String): Boolean {
        // TODO 3.1: Check if any task has matching title (case-insensitive)
        // Hint: return tasks.any { it.title.equals(title, ignoreCase = true) }
        TODO("Check for duplicate title")
    }

    // Test helper methods
    /**
     * TODO 3.1: Add helper method to add multiple tasks at once
     * This is useful for test setup.
     */
    fun addTasks(vararg tasksToAdd: Task) {
        // TODO 3.1: Add all tasks and update flow
        // Hint: tasks.addAll(tasksToAdd)
        // Hint: tasksFlow.value = tasks.toList()
    }

    /**
     * TODO 3.1: Add helper method to clear all tasks
     * This is useful for test teardown.
     */
    fun clearAll() {
        // TODO 3.1: Clear tasks and reset flow
        // Hint: tasks.clear()
        // Hint: tasksFlow.value = emptyList()
    }
}
