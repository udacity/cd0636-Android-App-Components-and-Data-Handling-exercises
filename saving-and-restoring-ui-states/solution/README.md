# Task Manager with State Preservation - Solution

## Overview

This is the complete solution for the Task Manager with State Preservation exercise that demonstrates using SavedStateHandle to survive process death. The application uses LiveData for reactive UI updates and adds SavedStateHandle for state preservation across process death scenarios.

## Exercise Instructions

This section provides detailed step-by-step instructions for completing each TODO in the exercise.

### Part 1: Add SavedStateHandle to TaskViewModel (TaskViewModel.kt)

#### Step 1: Update constructor to accept SavedStateHandle

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class TaskViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var nextTaskId = 4

    // Rest of implementation...
}
```

**Why?** SavedStateHandle is automatically provided by Android when using the `by viewModels()` delegate. By accepting it as a constructor parameter, the ViewModel gains access to persistent state storage that survives both configuration changes (like regular ViewModel) and process death (unlike regular ViewModel). This eliminates the need for manual onSaveInstanceState handling in the Activity.

#### Step 2: Replace MutableLiveData with SavedStateHandle.getLiveData()

```kotlin
class TaskViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        Task(id = 3, title = "Build task manager app", completed = false)
    )

    // Remove: private val _tasks = MutableLiveData<List<Task>>()
    // Remove: val tasks: LiveData<List<Task>> get() = _tasks

    // Replace with:
    val tasks: LiveData<List<Task>> = savedStateHandle.getLiveData("tasks", defaultTasks)

    val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.count { it.completed }
    }
}
```

**Why?** SavedStateHandle.getLiveData() creates a LiveData that automatically persists its value across process death. Unlike MutableLiveData which loses data when the system kills the app, SavedStateHandle saves data to a Bundle that Android restores when recreating the process. The second parameter provides the default value on first launch. This approach combines LiveData's reactive benefits with automatic state restoration.

#### Step 3: Update methods to use SavedStateHandle

```kotlin
fun addTask(title: String) {
    val currentTasks = tasks.value ?: emptyList()
    val newTask = Task(
        id = nextTaskId++,
        title = title,
        completed = false
    )
    val updatedTasks = currentTasks + newTask

    // OLD: _tasks.value = updatedTasks
    // NEW:
    savedStateHandle["tasks"] = updatedTasks
}

fun toggleTaskCompletion(taskId: Int) {
    val currentTasks = tasks.value ?: emptyList()
    val updatedTasks = currentTasks.map { task ->
        if (task.id == taskId) {
            task.copy(completed = !task.completed)
        } else {
            task
        }
    }
    savedStateHandle["tasks"] = updatedTasks
}

fun deleteTask(taskId: Int) {
    val currentTasks = tasks.value ?: emptyList()
    val updatedTasks = currentTasks.filter { it.id != taskId }
    savedStateHandle["tasks"] = updatedTasks
}
```

**Why?** Using SavedStateHandle's indexed access (`savedStateHandle["key"] = value`) automatically persists the data and notifies any LiveData observers created from the same key. This is simpler than the old approach of manually saving to Bundle in onSaveInstanceState. The updated data survives both configuration changes and process death, ensuring users never lose their task edits.

### Part 2: Make Task Parcelable (Task.kt)

#### Step 1: Add Parcelable implementation

```kotlin
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean
) : Parcelable
```

Note: Ensure `id 'kotlin-parcelize'` plugin is added to app-level build.gradle.

**Why?** SavedStateHandle stores data in a Bundle, which can only hold Parcelable types (or primitives). The @Parcelize annotation from Kotlin's parcelize plugin automatically generates the boilerplate code needed to make the Task class Parcelable. Without this, SavedStateHandle wouldn't be able to serialize and restore Task objects across process death. This is more efficient than using Serializable and requires minimal code.

#### Step 2: Verify computed properties still work

No code changes needed. The computed LiveData properties continue to work:

```kotlin
// In TaskViewModel - these still work with SavedStateHandle-backed LiveData
val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
    taskList.size
}

val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
    taskList.count { it.completed }
}
```

### Part 3: Update ViewModel Instantiation (MainActivity.kt)

#### Step 1: Use by viewModels() delegate

```kotlin
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // OLD: private lateinit var viewModel: TaskViewModel
    // NEW:
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    // ... other view references

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // OLD: viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        // Removed - ViewModel now initialized by delegate

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
}
```

**Why?** The `by viewModels()` delegate automatically provides SavedStateHandle to ViewModels that have it as a constructor parameter. This is cleaner than using ViewModelProvider manually and eliminates boilerplate code. The delegate lazily creates the ViewModel on first access and ensures it's properly retained across configuration changes. Most importantly, it automatically injects SavedStateHandle without any additional setup, making state preservation seamless.

#### Step 2: Remove onCreate ViewModel initialization

Already completed in Step 1 - simply remove the old ViewModelProvider line from onCreate.

## Complete File Examples

### TaskViewModel.kt (Complete)

```kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class TaskViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var nextTaskId = 4

    private val defaultTasks = listOf(
        Task(id = 1, title = "Complete Android lesson on ViewModels", completed = false),
        Task(id = 2, title = "Review MVVM architecture patterns", completed = true),
        Task(id = 3, title = "Build task manager app", completed = false)
    )

    val tasks: LiveData<List<Task>> = savedStateHandle.getLiveData("tasks", defaultTasks)

    val totalTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.size
    }

    val completedTaskCount: LiveData<Int> = tasks.map { taskList ->
        taskList.count { it.completed }
    }

    fun addTask(title: String) {
        val currentTasks = tasks.value ?: emptyList()
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )
        val updatedTasks = currentTasks + newTask
        savedStateHandle["tasks"] = updatedTasks
    }

    fun toggleTaskCompletion(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.map { task ->
            if (task.id == taskId) {
                task.copy(completed = !task.completed)
            } else {
                task
            }
        }
        savedStateHandle["tasks"] = updatedTasks
    }

    fun deleteTask(taskId: Int) {
        val currentTasks = tasks.value ?: emptyList()
        val updatedTasks = currentTasks.filter { it.id != taskId }
        savedStateHandle["tasks"] = updatedTasks
    }
}
```

### Task.kt (Complete)

```kotlin
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean
) : Parcelable
```

### MainActivity.kt (Complete)

```kotlin
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskToggled = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )

        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        addTaskButton.setOnClickListener {
            val title = taskInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTask(title)
                taskInputEditText.text.clear()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }

        viewModel.totalTaskCount.observe(this) { count ->
            totalTasksTextView.text = "Total: $count"
        }

        viewModel.completedTaskCount.observe(this) { count ->
            completedTasksTextView.text = "Completed: $count"
        }
    }
}
```

## Verification

To verify your implementation:

1. **Build succeeds** - App compiles without errors
2. **First launch** - Default tasks appear
3. **Add/modify tasks** - Changes persist
4. **Rotate device** - Tasks survive configuration changes
5. **Enable "Don't keep activities"** - Go to Settings → Developer Options → Enable "Don't keep activities", open another app, return to Task Manager - tasks persist
6. **Process death simulation** - Run `adb shell am kill com.yourpackage`, reopen app - tasks persist

## Architecture Pattern

This solution demonstrates state preservation across multiple scenarios:

- **Configuration changes** - Handled by ViewModel
- **Process death** - Handled by SavedStateHandle
- **Reactive updates** - Handled by LiveData observers

Data flow: User action → ViewModel method → SavedStateHandle update → LiveData notification → UI update → State persisted to Bundle