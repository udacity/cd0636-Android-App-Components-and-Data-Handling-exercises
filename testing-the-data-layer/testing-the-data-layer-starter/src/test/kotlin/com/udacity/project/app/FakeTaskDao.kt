package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of TaskDao for unit testing.
 * Uses in-memory storage to simulate database operations.
 */
class FakeTaskDao : TaskDao {
    private val tasks = mutableListOf<TaskEntity>()
    private val tasksFlow = MutableStateFlow<List<TaskEntity>>(emptyList())

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasksFlow

    override fun getFavoriteTasks(): Flow<List<TaskEntity>> {
        return tasksFlow.map { list -> list.filter { it.isFavorite } }
    }

    override fun getActiveTasks(): Flow<List<TaskEntity>> {
        return tasksFlow.map { list -> list.filter { !it.completed } }
    }

    override suspend fun getTaskById(taskId: Int): TaskEntity? {
        return tasks.find { it.id == taskId }
    }

    override suspend fun countTasksWithTitle(title: String): Int {
        return tasks.count { it.title.equals(title, ignoreCase = true) }
    }

    override suspend fun insertTask(task: TaskEntity) {
        tasks.add(task)
        updateFlow()
    }

    override suspend fun updateTask(task: TaskEntity) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            updateFlow()
        }
    }

    override suspend fun deleteTaskById(taskId: Int) {
        tasks.removeIf { it.id == taskId }
        updateFlow()
    }

    // Test helper methods
    /**
     * Clears all tasks from the fake database.
     */
    fun clearAll() {
        tasks.clear()
        tasksFlow.value = emptyList()
    }

    /**
     * Returns a copy of all tasks for verification in tests.
     */
    fun getTasks(): List<TaskEntity> = tasks.toList()

    /**
     * Updates the Flow to emit the current task list.
     */
    private fun updateFlow() {
        tasksFlow.value = tasks.toList()
    }
}
