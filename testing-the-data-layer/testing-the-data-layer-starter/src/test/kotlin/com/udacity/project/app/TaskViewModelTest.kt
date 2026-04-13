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

/**
 * Unit tests for TaskViewModel.
 * Uses Turbine for testing StateFlows backed by WhileSubscribed.
 *
 * TODO Part 3: ViewModel Tests
 * Complete this test class to verify ViewModel behavior using FakeTaskRepository.
 *
 * Key pattern: add data INSIDE the .test {} block after consuming the initial
 * emission. This ensures WhileSubscribed has activated and tasks.value is
 * populated before calling actions like toggleTaskCompletion that read tasks.value.
 */
@ExperimentalCoroutinesApi
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var fakeRepository: FakeTaskRepository

    @Before
    fun setup() {
        // TODO 3.2: Set up test environment
        // Hint: Dispatchers.setMain(UnconfinedTestDispatcher())
        // Hint: fakeRepository = FakeTaskRepository()
        // Hint: viewModel = TaskViewModel(repository = fakeRepository, savedStateHandle = SavedStateHandle())
    }

    @After
    fun tearDown() {
        // TODO 3.2: Clean up test dispatcher
        // Hint: Dispatchers.resetMain()
    }

    @Test
    fun `tasks flow emits tasks from repository`() = runTest {
        // TODO 3.3: Test that ViewModel's tasks StateFlow emits repository data
        // Hint:
        // viewModel.tasks.test {
        //     assertThat(awaitItem()).isEmpty()  // initial empty list
        //
        //     fakeRepository.addTasks(
        //         Task(1, "Task 1", false, false),
        //         Task(2, "Task 2", true, false)
        //     )
        //
        //     val tasks = awaitItem()
        //     assertThat(tasks).hasSize(2)
        //     assertThat(tasks[0].title).isEqualTo("Task 1")
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `addTask adds task to repository`() = runTest {
        // TODO 3.3: Test that addTask stores task in repository
        // Hint:
        // viewModel.tasks.test {
        //     awaitItem() // initial empty
        //     viewModel.addTask("New Task")
        //     val tasks = awaitItem()
        //     assertThat(tasks).hasSize(1)
        //     assertThat(tasks[0].title).isEqualTo("New Task")
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `addTask trims whitespace from title`() = runTest {
        // TODO 3.3: Test that addTask trims whitespace
        // When: Add task with spaces: "  Task with spaces  "
        // Then: Use Turbine to verify title is trimmed to "Task with spaces"
    }

    @Test
    fun `toggleTaskCompletion marks incomplete task as complete`() = runTest {
        // TODO 3.3: Test toggling task completion status
        // Important: Add data INSIDE .test {} so tasks.value is populated
        // Hint:
        // viewModel.tasks.test {
        //     assertThat(awaitItem()).isEmpty()
        //     fakeRepository.addTasks(Task(1, "Task", false, false))
        //     awaitItem() // [task] -- tasks.value is now populated
        //     viewModel.toggleTaskCompletion(1)
        //     val tasks = awaitItem()
        //     assertThat(tasks[0].completed).isTrue()
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `toggleTaskCompletion marks complete task as incomplete`() = runTest {
        // TODO 3.3: Test toggling complete task to incomplete
        // Given: Add Task(1, "Task", completed=true, isFavorite=false) inside .test {}
        // Then: After toggle, verify completed is false
    }

    @Test
    fun `toggleTaskFavorite marks non-favorite as favorite`() = runTest {
        // TODO 3.3: Test toggling favorite status
        // Given: Add non-favorite task inside .test {}
        // When: Call viewModel.toggleTaskFavorite(taskId)
        // Then: Use Turbine to verify isFavorite is true
    }

    @Test
    fun `deleteTask removes task from repository`() = runTest {
        // TODO 3.3: Test deleting a task
        // Hint:
        // viewModel.tasks.test {
        //     assertThat(awaitItem()).isEmpty()
        //     fakeRepository.addTasks(Task(1, "Task to delete", false, false))
        //     assertThat(awaitItem()).hasSize(1)
        //     viewModel.deleteTask(1)
        //     assertThat(awaitItem()).isEmpty()
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `totalTaskCount emits correct count`() = runTest {
        // TODO 3.3: Test totalTaskCount StateFlow
        // Hint:
        // viewModel.totalTaskCount.test {
        //     assertThat(awaitItem()).isEqualTo(0) // initial
        //     fakeRepository.addTasks(
        //         Task(1, "Task 1", false, false),
        //         Task(2, "Task 2", false, false),
        //         Task(3, "Task 3", true, false)
        //     )
        //     assertThat(awaitItem()).isEqualTo(3)
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `completedTaskCount emits correct count`() = runTest {
        // TODO 3.3: Test completedTaskCount StateFlow
        // Hint:
        // viewModel.completedTaskCount.test {
        //     assertThat(awaitItem()).isEqualTo(0) // initial
        //     fakeRepository.addTasks(
        //         Task(1, "Task 1", false, false),
        //         Task(2, "Task 2", true, false),
        //         Task(3, "Task 3", true, false)
        //     )
        //     assertThat(awaitItem()).isEqualTo(2)
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `completedTaskCount updates when task is toggled`() = runTest {
        // TODO 3.3: Test that completedTaskCount updates reactively
        // Important: Use tasks.test {} so tasks.value is populated for toggle
        // Hint:
        // viewModel.tasks.test {
        //     assertThat(awaitItem()).isEmpty()
        //     fakeRepository.addTasks(Task(1, "Task", false, false))
        //     awaitItem() // tasks.value populated
        //     viewModel.toggleTaskCompletion(1)
        //     val tasks = awaitItem()
        //     assertThat(tasks.count { it.completed }).isEqualTo(1)
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `multiple operations maintain state consistency`() = runTest {
        // TODO 3.3: Test multiple operations work together correctly
        // Hint:
        // viewModel.tasks.test {
        //     assertThat(awaitItem()).isEmpty()
        //     viewModel.addTask("Task 1")
        //     awaitItem()
        //     viewModel.addTask("Task 2")
        //     val tasksAfterAdd = awaitItem()
        //     val task1Id = tasksAfterAdd.find { it.title == "Task 1" }?.id ?: 0
        //     viewModel.toggleTaskCompletion(task1Id)
        //     val tasks = awaitItem()
        //     assertThat(tasks.find { it.title == "Task 1" }?.completed).isTrue()
        //     assertThat(tasks.find { it.title == "Task 2" }?.completed).isFalse()
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `deleting task updates totalTaskCount`() = runTest {
        // TODO 3.3: Test that derived state updates when tasks change
        // Hint:
        // viewModel.totalTaskCount.test {
        //     assertThat(awaitItem()).isEqualTo(0)
        //     fakeRepository.addTasks(
        //         Task(1, "Task 1", false, false),
        //         Task(2, "Task 2", false, false)
        //     )
        //     assertThat(awaitItem()).isEqualTo(2)
        //     viewModel.deleteTask(1)
        //     assertThat(awaitItem()).isEqualTo(1)
        //     cancelAndIgnoreRemainingEvents()
        // }
    }

    @Test
    fun `ViewModel handles empty repository`() = runTest {
        // TODO 3.3: Test ViewModel with empty repository
        // Hint: Use Turbine on tasks, totalTaskCount, completedTaskCount
        // Verify all are empty/zero on the first awaitItem()
    }
}