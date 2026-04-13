package com.udacity.project.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Interface for managing task data.
 * Acts as a single source of truth for task data.
 */
interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getFavoriteTasks(): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Int): Task?
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Int)
    suspend fun isTaskTitleDuplicate(title: String): Boolean
}

/**
 * Implementation of TaskRepository using Room database.
 */
class DefaultTaskRepository(
    private val taskDao: TaskDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFavoriteTasks(): Flow<List<Task>> {
        return taskDao.getFavoriteTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTaskById(taskId: Int): Task? = withContext(dispatcher) {
        taskDao.getTaskById(taskId)?.toDomainModel()
    }

    override suspend fun addTask(task: Task) = withContext(dispatcher) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) = withContext(dispatcher) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: Int) = withContext(dispatcher) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun isTaskTitleDuplicate(title: String): Boolean = withContext(dispatcher) {
        taskDao.countTasksWithTitle(title) > 0
    }
}