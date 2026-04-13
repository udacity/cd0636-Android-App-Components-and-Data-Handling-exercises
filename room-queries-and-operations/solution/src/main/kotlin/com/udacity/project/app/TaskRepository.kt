package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing task data.
 * Acts as a single source of truth for task data.
 */
class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
}