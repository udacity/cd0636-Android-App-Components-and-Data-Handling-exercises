package com.udacity.project.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Task database operations.
 *
 * Complete Part 1 TODOs to add CRUD operations.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * TODO 1.1: Add insertTask()
     *
     * Add @Insert annotation with OnConflictStrategy.REPLACE
     * Function signature: suspend fun insertTask(task: TaskEntity)
     */
    // suspend fun insertTask(task: TaskEntity)

    /**
     * TODO 1.2: Add updateTask()
     *
     * Add @Update annotation
     * Function signature: suspend fun updateTask(task: TaskEntity)
     */
    // suspend fun updateTask(task: TaskEntity)

    /**
     * TODO 1.3: Add deleteTaskById()
     *
     * Add @Query annotation with: DELETE FROM tasks WHERE id = :taskId
     * Function signature: suspend fun deleteTaskById(taskId: Int)
     */
    // suspend fun deleteTaskById(taskId: Int)
}