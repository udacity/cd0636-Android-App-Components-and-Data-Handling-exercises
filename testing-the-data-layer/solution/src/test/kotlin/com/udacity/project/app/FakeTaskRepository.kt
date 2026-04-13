package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of TaskRepository for testing.
 * Provides in-memory storage with controllable behavior.
 */
class FakeTaskRepository : TaskRepository {

    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow

    override fun getFavoriteTasks(): Flow<List<Task>> {
        return tasksFlow.map { list -> list.filter { it.isFavorite } }
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return tasksFlow.map { list -> list.filter { !it.completed } }
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return tasks.find { it.id == taskId }
    }

    override suspend fun addTask(task: Task) {
        tasks.add(task)
        tasksFlow.value = tasks.toList()
    }

    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            tasksFlow.value = tasks.toList()
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        tasks.removeIf { it.id == taskId }
        tasksFlow.value = tasks.toList()
    }

    override suspend fun isTaskTitleDuplicate(title: String): Boolean {
        return tasks.any { it.title.equals(title, ignoreCase = true) }
    }

    // Test helper methods
    fun addTasks(vararg tasksToAdd: Task) {
        tasks.addAll(tasksToAdd)
        tasksFlow.value = tasks.toList()
    }

    fun clearAll() {
        tasks.clear()
        tasksFlow.value = emptyList()
    }
}
