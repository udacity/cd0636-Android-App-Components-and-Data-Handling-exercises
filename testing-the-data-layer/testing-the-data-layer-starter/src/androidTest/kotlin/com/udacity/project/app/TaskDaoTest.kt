package com.udacity.project.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for TaskDao.
 * Uses in-memory Room database to test actual SQL queries.
 *
 * TODO Part 2: DAO Instrumented Tests
 * Complete this test class to verify DAO SQL operations with a real Room database.
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        // TODO 2.1: Create in-memory database and get DAO
        // Hint: Use Room.inMemoryDatabaseBuilder()
        // Hint: Call .allowMainThreadQueries() for testing
        // Hint: Get taskDao from database
    }

    @After
    fun tearDown() {
        // TODO 2.1: Close database to prevent leaks
        // Hint: database.close()
    }

    @Test
    fun insertTask_andGetById_returnsTask() = runBlocking {
        // TODO 2.2: Test inserting a task and retrieving it by ID
        // Given: Create a TaskEntity with all fields set

        // When: Insert task using taskDao.insertTask()
        // Then: Retrieve task by ID using taskDao.getTaskById()

        // Verify: Task is not null and all fields match
    }

    @Test
    fun getAllTasks_returnsTasksOrderedByCreatedAt() = runBlocking {
        // TODO 2.2: Test that tasks are ordered by createdAt DESC
        // Given: Insert 3 tasks with different createdAt timestamps
        // Hint: Use Thread.sleep(10) between inserts for distinct timestamps

        // When: Call taskDao.getAllTasks().first()

        // Then: Verify tasks are in descending order (newest first)
    }

    @Test
    fun getFavoriteTasks_returnsOnlyFavorites() = runBlocking {
        // TODO 2.2: Test filtering by favorite flag
        // Given: Insert 2 favorite tasks and 1 non-favorite task

        // When: Call taskDao.getFavoriteTasks().first()

        // Then: Verify only 2 favorite tasks are returned
    }

    @Test
    fun getActiveTasks_returnsOnlyIncomplete() = runBlocking {
        // TODO 2.2: Test filtering by completion status
        // Given: Insert 2 incomplete tasks and 1 completed task

        // When: Call taskDao.getActiveTasks().first()

        // Then: Verify only 2 incomplete tasks are returned
    }

    @Test
    fun updateTask_updatesTaskInDatabase() = runBlocking {
        // TODO 2.2: Test updating a task
        // Given: Insert a task with completed=false

        // When: Update task to completed=true and call taskDao.updateTask()

        // Then: Retrieve task and verify completed=true
    }

    @Test
    fun deleteTask_removesTaskFromDatabase() = runBlocking {
        // TODO 2.2: Test deleting a task
        // Given: Insert 2 tasks

        // When: Delete one task using taskDao.deleteTaskById()

        // Then: Verify only 1 task remains
    }

    @Test
    fun countTasksWithTitle_returnCorrectCount() = runBlocking {
        // TODO 2.2: Test duplicate title detection (case-insensitive)
        // Given: Insert tasks with titles "Task", "task", and "Other"

        // When: Call taskDao.countTasksWithTitle("TASK")

        // Then: Verify count is 2 (case-insensitive)
    }
}
