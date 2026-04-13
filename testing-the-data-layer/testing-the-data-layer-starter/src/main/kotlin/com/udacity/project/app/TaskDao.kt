package com.udacity.project.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Task database operations.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY created_at DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("SELECT COUNT(*) FROM tasks WHERE LOWER(title) = LOWER(:title)")
    suspend fun countTasksWithTitle(title: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}