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
 *
 * TODO Part 1: Repository Tests
 * Complete this test class to verify Repository behavior using the provided FakeTaskDao.
 */
@ExperimentalCoroutinesApi
class TaskRepositoryTest {

    private lateinit var repository: TaskRepository
    private lateinit var fakeDao: FakeTaskDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // TODO 1.1: Initialize FakeTaskDao and DefaultTaskRepository
        // Hint: fakeDao = FakeTaskDao()
        // Hint: repository = DefaultTaskRepository(fakeDao, testDispatcher)
    }

    @After
    fun tearDown() {
        // TODO 1.1: Clean up FakeTaskDao after each test
        // Hint: fakeDao.clearAll()
    }

    @Test
    fun `addTask inserts task into database`() = runTest(testDispatcher) {
        // TODO 1.2: Test that adding a task stores it correctly
        // Given: Create a Task with id=1, title="Test Task", completed=false, isFavorite=false

        // When: Call repository.addTask(task)

        // Then: Verify the task is in fakeDao.getTasks()
        // Hint: Use assertThat(tasks).hasSize(1)
        // Hint: Verify task properties match expected values
    }

    @Test
    fun `getAllTasks returns all tasks from database`() = runTest(testDispatcher) {
        // TODO 1.2: Test that getAllTasks returns all tasks
        // Given: Insert 2 TaskEntity objects directly into fakeDao
        // Hint: Create TaskEntity with createdAt = System.currentTimeMillis()

        // When: Call repository.getAllTasks().first()

        // Then: Verify 2 tasks are returned with correct properties
    }

    @Test
    fun `updateTask updates existing task in database`() = runTest(testDispatcher) {
        // TODO 1.2: Test that updating a task changes its properties
        // Given: Insert a TaskEntity with original values into fakeDao

        // When: Create an updated Task and call repository.updateTask()

        // Then: Verify the task in fakeDao has updated values
    }

    @Test
    fun `deleteTask removes task from database`() = runTest(testDispatcher) {
        // TODO 1.2: Test that deleting a task removes it
        // Given: Insert 2 TaskEntity objects into fakeDao

        // When: Call repository.deleteTask(taskId)

        // Then: Verify only 1 task remains with the correct id
    }

    @Test
    fun `isTaskTitleDuplicate returns true when duplicate exists`() = runTest(testDispatcher) {
        // TODO 1.2: Test duplicate detection
        // Given: Add a task with title "Existing Task"

        // When: Call repository.isTaskTitleDuplicate("existing task") (different case)

        // Then: Verify it returns true (case-insensitive)
    }

    @Test
    fun `isTaskTitleDuplicate returns false when no duplicate`() = runTest(testDispatcher) {
        // TODO 1.2: Test non-duplicate scenario
        // Given: Add a task with title "Task 1"

        // When: Call repository.isTaskTitleDuplicate("Task 2")

        // Then: Verify it returns false
    }
}
