# Task Manager with ViewModel - Solution

## Overview

This is the complete solution for the Task Manager exercise that demonstrates proper MVVM
architecture using ViewModel. The application survives configuration changes (like
screen rotation) and follows Android best practices for separating business logic from UI code.

## Exercise Instructions

This section provides detailed step-by-step instructions for completing each TODO in the exercise.
Follow these steps to understand how the solution was implemented.

### Part 1: TaskViewModel - Business Logic and Data Management

Complete the ViewModel that manages task data and business logic.

#### Step 1.1: Make the class extend ViewModel

Open `TaskViewModel.kt` and make the class extend `ViewModel` from the AndroidX lifecycle library.

```kotlin
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    // ...
}
```

**Why?** Extending `ViewModel` allows this class to survive configuration changes like screen
rotations. The Android system automatically retains ViewModel instances across configuration
changes.

#### Step 1.2: Complete the addTask method

In the `addTask()` method, add the new task to the `_tasks` list.

```kotlin
fun addTask(title: String) {
    val newTask = Task(
        id = nextTaskId++,
        title = title,
        completed = false
    )

    // Add this line:
    _tasks.add(newTask)
}
```

**Why?** The ViewModel manages the data and provides methods to manipulate it. This method handles
the business logic for adding a new task.

#### Step 1.3: Complete the toggleTaskCompletion method

In the `toggleTaskCompletion()` method, find the task by ID and toggle its completion status.

```kotlin
fun toggleTaskCompletion(taskId: Int) {
    // Add these lines:
    val index = _tasks.indexOfFirst { it.id == taskId }
    if (index != -1) {
        val task = _tasks[index]
        _tasks[index] = task.copy(completed = !task.completed)
    }
}
```

**Why?** Using the data class `copy()` method creates a new instance with the modified field,
following immutability best practices.

#### Step 1.4: Complete the deleteTask method

In the `deleteTask()` method, remove the task with the specified ID.

```kotlin
fun deleteTask(taskId: Int) {
    // Add this line:
    _tasks.removeIf { it.id == taskId }
}
```

**Why?** The `removeIf` function provides a clean, functional approach to removing items from
a mutable list.

### Part 2: MainActivity - UI Layer and ViewModel Integration

Connect the UI to the ViewModel and handle user interactions.

#### Step 2.1: Get ViewModel instance using ViewModelProvider

In `onCreate()`, obtain the ViewModel instance using ViewModelProvider.

```kotlin
import androidx.lifecycle.ViewModelProvider

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Add this line:
    viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

}
```

**Why?** `ViewModelProvider` is responsible for creating or retrieving ViewModels. It ensures that
the same ViewModel instance is returned when the Activity is recreated during configuration changes.
This is how data survives screen rotations.

#### Step 2.2: Call ViewModel methods in adapter callbacks

In `setupRecyclerView()`, connect the adapter callbacks to the corresponding ViewModel methods.

```kotlin
private fun setupRecyclerView() {
    taskAdapter = TaskAdapter(
        onTaskToggled = { taskId ->
            viewModel.toggleTaskCompletion(taskId)  // Add this
            updateUI()  // Add this
        },
        onTaskDeleted = { taskId ->
            viewModel.deleteTask(taskId)  // Add this
            updateUI()  // Add this
        }
    )
    // ...
}
```

**Why?** Instead of managing data in the Activity, we delegate all business logic to the ViewModel.
The Activity only handles UI events and forwards them to the ViewModel. After the ViewModel updates
the data, we call `updateUI()` to refresh the display.

#### Step 2.3: Call ViewModel addTask method

In `setupClickListeners()`, implement the add button click handler.

```kotlin
private fun setupClickListeners() {
    addTaskButton.setOnClickListener {
        val title = taskInputEditText.text.toString().trim()
        if (title.isNotEmpty()) {
            viewModel.addTask(title)  // Add this
            updateUI()  // Add this
            taskInputEditText.text.clear()
        }
    }
}
```

**Why?** When the user clicks the add button, we forward the action to the ViewModel,
which handles creating the task. After adding, we call `updateUI()` to refresh the display.

#### Step 2.4: Complete the updateUI method

In `updateUI()`, get data from the ViewModel and update all UI elements.

```kotlin
private fun updateUI() {
    // Get tasks from ViewModel and update adapter
    taskAdapter.updateTasks(viewModel.getTasks())

    // Get counts and update TextViews
    totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"
    completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
}
```

**Why?** The `updateUI()` method pulls data from the ViewModel and updates all the UI elements.
This method is called whenever the data changes, ensuring the UI stays in sync with the ViewModel's state.