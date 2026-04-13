package com.udacity.project.app

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TaskRepository.
 * Uses FakeTaskDao to test repository logic without real database.
 */
@ExperimentalCoroutinesApi
class TaskRepositoryTest {

    private lateinit var repository: TaskRepository
    private lateinit var fakeDao: FakeTaskDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakeDao = FakeTaskDao()
        repository = DefaultTaskRepository(fakeDao, testDispatcher)
    }

    @After
    fun tearDown() {
        fakeDao.clearAll()
    }

    @Test
    fun `addTask inserts task into database`() = runTest(testDispatcher) {
        // Given
        val task = Task(
            id = 1,
            title = "Test Task",
            completed = false,
            isFavorite = false
        )

        // When
        repository.addTask(task)

        // Then
        val tasks = fakeDao.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo("Test Task")
        assertThat(tasks[0].completed).isFalse()
    }

    @Test
    fun `getAllTasks returns all tasks from database`() = runTest(testDispatcher) {
        // Given
        val task1 = TaskEntity(
            id = 1,
            title = "Task 1",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )
        val task2 = TaskEntity(
            id = 2,
            title = "Task 2",
            completed = true,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )
        fakeDao.insertTask(task1)
        fakeDao.insertTask(task2)

        // When
        val tasks = repository.getAllTasks().first()

        // Then
        assertThat(tasks).hasSize(2)
        assertThat(tasks[0].title).isEqualTo("Task 1")
        assertThat(tasks[0].completed).isFalse()
        assertThat(tasks[1].title).isEqualTo("Task 2")
        assertThat(tasks[1].completed).isTrue()
    }

    @Test
    fun `updateTask updates existing task in database`() = runTest(testDispatcher) {
        // Given
        val originalTask = TaskEntity(
            id = 1,
            title = "Original Title",
            completed = false,
            isFavorite = false,
            createdAt = System.currentTimeMillis()
        )
        fakeDao.insertTask(originalTask)

        // When
        val updatedTask = Task(
            id = 1,
            title = "Updated Title",
            completed = true,
            isFavorite = false
        )
        repository.updateTask(updatedTask)

        // Then
        val tasks = fakeDao.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo("Updated Title")
        assertThat(tasks[0].completed).isTrue()
    }

    @Test
    fun `deleteTask removes task from database`() = runTest(testDispatcher) {
        // Given
        val task1 = TaskEntity(1, "Task 1", false, false, System.currentTimeMillis())
        val task2 = TaskEntity(2, "Task 2", false, false, System.currentTimeMillis())
        fakeDao.insertTask(task1)
        fakeDao.insertTask(task2)

        // When
        repository.deleteTask(1)

        // Then
        val tasks = fakeDao.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].id).isEqualTo(2)
        assertThat(tasks[0].title).isEqualTo("Task 2")
    }

    @Test
    fun `getAllTasks returns empty list when database is empty`() = runTest(testDispatcher) {
        // Given - empty database

        // When
        val tasks = repository.getAllTasks().first()

        // Then
        assertThat(tasks).isEmpty()
    }

    @Test
    fun `addTask with favorite flag stores favorite correctly`() = runTest(testDispatcher) {
        // Given
        val favoriteTask = Task(
            id = 1,
            title = "Favorite Task",
            completed = false,
            isFavorite = true
        )

        // When
        repository.addTask(favoriteTask)

        // Then
        val tasks = fakeDao.getTasks()
        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].isFavorite).isTrue()
    }

    @Test
    fun `multiple addTask operations store all tasks`() = runTest(testDispatcher) {
        // Given
        val task1 = Task(1, "Task 1", false, false)
        val task2 = Task(2, "Task 2", false, false)
        val task3 = Task(3, "Task 3", true, false)

        // When
        repository.addTask(task1)
        repository.addTask(task2)
        repository.addTask(task3)

        // Then
        val tasks = fakeDao.getTasks()
        assertThat(tasks).hasSize(3)
    }

    @Test
    fun `deleteTask on non-existent task does not throw error`() = runTest(testDispatcher) {
        // Given - empty database

        // When
        repository.deleteTask(999)

        // Then - no exception thrown
        val tasks = fakeDao.getTasks()
        assertThat(tasks).isEmpty()
    }
}
