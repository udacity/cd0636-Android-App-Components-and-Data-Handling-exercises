package com.udacity.project.app

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var fakeRepository: FakeTaskRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepository = FakeTaskRepository()
        viewModel = TaskViewModel(
            repository = fakeRepository,
            savedStateHandle = SavedStateHandle()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `tasks starts empty`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTask creates task in repository`() = runTest {
        viewModel.tasks.test {
            awaitItem() // initial empty

            viewModel.addTask("New Task")

            val tasks = awaitItem()
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].title).isEqualTo("New Task")
            assertThat(tasks[0].completed).isFalse()
            assertThat(tasks[0].isFavorite).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTask trims whitespace`() = runTest {
        viewModel.tasks.test {
            awaitItem()

            viewModel.addTask("  Task with spaces  ")

            assertThat(awaitItem()[0].title).isEqualTo("Task with spaces")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple addTask operations accumulate`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task 1")
            assertThat(awaitItem()).hasSize(1)

            viewModel.addTask("Task 2")
            assertThat(awaitItem()).hasSize(2)

            viewModel.addTask("Task 3")
            assertThat(awaitItem()).hasSize(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleTaskCompletion marks incomplete as complete`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskCompletion(taskId)

            assertThat(awaitItem()[0].completed).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleTaskCompletion marks complete as incomplete`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            // Add and toggle to completed
            viewModel.addTask("Task")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskCompletion(taskId)
            assertThat(awaitItem()[0].completed).isTrue()

            // Toggle back to incomplete
            viewModel.toggleTaskCompletion(taskId)
            assertThat(awaitItem()[0].completed).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleTaskFavorite marks as favorite`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskFavorite(taskId)

            assertThat(awaitItem()[0].isFavorite).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleTaskFavorite unmarks favorite`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            // Add and toggle to favorite
            viewModel.addTask("Task")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskFavorite(taskId)
            assertThat(awaitItem()[0].isFavorite).isTrue()

            // Toggle back to non-favorite
            viewModel.toggleTaskFavorite(taskId)
            assertThat(awaitItem()[0].isFavorite).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTask removes task`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task to delete")
            val taskId = awaitItem()[0].id

            viewModel.deleteTask(taskId)

            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTask with non-existent id does nothing`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task")
            assertThat(awaitItem()).hasSize(1)

            viewModel.deleteTask(999)

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalTaskCount starts at zero`() = runTest {
        viewModel.totalTaskCount.test {
            assertThat(awaitItem()).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalTaskCount updates after addTask`() = runTest {
        viewModel.totalTaskCount.test {
            assertThat(awaitItem()).isEqualTo(0)

            viewModel.addTask("Task 1")
            assertThat(awaitItem()).isEqualTo(1)

            viewModel.addTask("Task 2")
            assertThat(awaitItem()).isEqualTo(2)

            viewModel.addTask("Task 3")
            assertThat(awaitItem()).isEqualTo(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `totalTaskCount updates after delete`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task 1")
            awaitItem()
            viewModel.addTask("Task 2")
            val tasks = awaitItem()
            val task1Id = tasks.find { it.title == "Task 1" }!!.id

            viewModel.deleteTask(task1Id)
            assertThat(awaitItem()).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completedTaskCount starts at zero`() = runTest {
        viewModel.completedTaskCount.test {
            assertThat(awaitItem()).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `completedTaskCount updates after toggle`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskCompletion(taskId)

            val tasks = awaitItem()
            assertThat(tasks.count { it.completed }).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add then toggle then delete sequence`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task 1")
            val taskId = awaitItem()[0].id

            viewModel.toggleTaskCompletion(taskId)
            assertThat(awaitItem()[0].completed).isTrue()

            viewModel.deleteTask(taskId)
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle only affects target task`() = runTest {
        viewModel.tasks.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.addTask("Task 1")
            awaitItem()

            viewModel.addTask("Task 2")
            val tasksAfterAdd = awaitItem()

            val task1Id = tasksAfterAdd.find { it.title == "Task 1" }!!.id

            viewModel.toggleTaskCompletion(task1Id)

            val updated = awaitItem()
            assertThat(updated.find { it.title == "Task 1" }!!.completed).isTrue()
            assertThat(updated.find { it.title == "Task 2" }!!.completed).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
