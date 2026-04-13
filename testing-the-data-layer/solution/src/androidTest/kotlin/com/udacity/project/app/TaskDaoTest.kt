package com.udacity.project.app

import android.content.Context
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
 * Instrumented tests for TaskDao using in-memory Room database.
 * These tests verify actual database behavior.
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            context,
            TaskDatabase::class.java
        ).allowMainThreadQueries() // Only for testing
            .build()

        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTask_andGetAllTasks_returnsTask() = runBlocking {
        // Given
        val task = TaskEntity(
            id = 1,
            title = "Test Task",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )

        // When
        taskDao.insertTask(task)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo("Test Task")
        assertThat(tasks[0].completed).isFalse()
    }

    @Test
    fun getAllTasks_returnsTasksOrderedByCreatedAtDescending() = runBlocking {
        // Given
        val oldTask = TaskEntity(
            id = 1,
            title = "Old Task",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis() - 10000
        )
        val newTask = TaskEntity(
            id = 2,
            title = "New Task",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )

        // Insert old task first, then new task
        taskDao.insertTask(oldTask)
        taskDao.insertTask(newTask)

        // When
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(2)
        // Newest task should be first (descending order)
        assertThat(tasks[0].title).isEqualTo("New Task")
        assertThat(tasks[1].title).isEqualTo("Old Task")
    }

    @Test
    fun updateTask_updatesTaskInDatabase() = runBlocking {
        // Given
        val originalTask = TaskEntity(
            id = 1,
            title = "Original",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )
        taskDao.insertTask(originalTask)

        // When
        val updatedTask = originalTask.copy(
            title = "Updated",
            completed = true
        )
        taskDao.updateTask(updatedTask)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo("Updated")
        assertThat(tasks[0].completed).isTrue()
    }

    @Test
    fun deleteTaskById_removesTaskFromDatabase() = runBlocking {
        // Given
        val task1 = TaskEntity(1, "Task 1", false, false, System.currentTimeMillis())
        val task2 = TaskEntity(2, "Task 2", false, false, System.currentTimeMillis())
        taskDao.insertTask(task1)
        taskDao.insertTask(task2)

        // When
        taskDao.deleteTaskById(1)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(2)
    }

    @Test
    fun insertTask_withReplaceStrategy_replacesExistingTask() = runBlocking {
        // Given
        val task = TaskEntity(1, "Original", false, false, System.currentTimeMillis())
        taskDao.insertTask(task)

        // When - insert task with same ID (should replace due to OnConflictStrategy.REPLACE)
        val replacementTask = TaskEntity(1, "Replaced", true, false, System.currentTimeMillis())
        taskDao.insertTask(replacementTask)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo("Replaced")
        assertThat(tasks[0].completed).isTrue()
    }

    @Test
    fun insertTask_withFavoriteFlag_storesFavoriteCorrectly() = runBlocking {
        // Given
        val favoriteTask = TaskEntity(
            id = 1,
            title = "Favorite Task",
            completed = false,
            isFavorite = true,
            createdAt = System.currentTimeMillis()
        )

        // When
        taskDao.insertTask(favoriteTask)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].isFavorite).isTrue()
    }

    @Test
    fun deleteTaskById_withNonExistentId_doesNothing() = runBlocking {
        // Given
        val task = TaskEntity(1, "Task 1", false, false, System.currentTimeMillis())
        taskDao.insertTask(task)

        // When
        taskDao.deleteTaskById(999) // Non-existent ID

        // Then
        val tasks = taskDao.getAllTasks().first()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(1)
    }

    @Test
    fun getAllTasks_withEmptyDatabase_returnsEmptyList() = runBlocking {
        // Given - empty database

        // When
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).isEmpty()
    }

    @Test
    fun insertMultipleTasks_andGetAllTasks_returnsAllTasks() = runBlocking {
        // Given
        val task1 = TaskEntity(1, "Task 1", false, false, System.currentTimeMillis())
        val task2 = TaskEntity(2, "Task 2", true, false, System.currentTimeMillis())
        val task3 = TaskEntity(3, "Task 3", false, true, System.currentTimeMillis())

        // When
        taskDao.insertTask(task1)
        taskDao.insertTask(task2)
        taskDao.insertTask(task3)
        val tasks = taskDao.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(3)
    }
}
