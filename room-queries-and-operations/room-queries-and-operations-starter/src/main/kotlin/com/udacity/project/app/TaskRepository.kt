package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing task data.
 * Acts as a single source of truth for task data.
 *
 * Complete Part 1 TODOs to add CRUD operations.
 */
class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * TODO 1.4: Implement addTask()
     *
     * Create suspend function that:
     * 1. Converts Task to TaskEntity using task.toEntity()
     * 2. Calls taskDao.insertTask()
     */
    // suspend fun addTask(task: Task)

    /**
     * TODO 1.5: Implement updateTask()
     *
     * Create suspend function that:
     * 1. Converts Task to TaskEntity using task.toEntity()
     * 2. Calls taskDao.updateTask()
     */
    // suspend fun updateTask(task: Task)

    /**
     * TODO 1.6: Implement deleteTask()
     *
     * Create suspend function that calls taskDao.deleteTaskById(taskId)
     */
    // suspend fun deleteTask(taskId: Int)
}