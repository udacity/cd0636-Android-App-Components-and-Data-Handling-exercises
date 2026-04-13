# LiveData Solution - Part 1

This folder contains the complete LiveData implementation for Exercise 5, Part 1.

## What Goes Here

This folder should contain the complete Android project implementing the LiveData approach:

- **TaskViewModel.kt** - ViewModel using `MutableLiveData` and `LiveData`
- **MainActivity.kt** - Activity observing LiveData with `.observe(this) { }`
- **Task.kt** - Data class for task model
- **TaskAdapter.kt** - RecyclerView adapter for displaying tasks
- **layout/activity_main.xml** - Main layout
- **layout/item_task.xml** - Task item layout
- Other supporting files and resources

## Quick Implementation Summary

### TaskViewModel.kt
```kotlin
class TaskViewModel : ViewModel() {
    // Private mutable, public read-only
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    // Computed properties using transformations
    val totalTaskCount: LiveData<Int> = _tasks.map { it.size }
    val completedTaskCount: LiveData<Int> = _tasks.map { it.count { it.completed } }

    init {
        _tasks.value = listOf(/* sample tasks */)
    }

    fun addTask(title: String) { /* update _tasks.value */ }
    fun toggleTaskCompletion(taskId: Int) { /* update _tasks.value */ }
    fun deleteTask(taskId: Int) { /* update _tasks.value */ }
}
```

### MainActivity.kt
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... setup ...
        observeViewModel()
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

## Key Concepts

**LiveData Benefits:**
- Automatic lifecycle awareness
- No manual cleanup needed
- Simple `.observe(this) { }` syntax
- Built-in null safety handling

**Important Patterns:**
- Encapsulation: Private `MutableLiveData`, public `LiveData`
- Reactivity: Set `.value` to trigger observers automatically
- Transformations: Use `.map()` for computed properties
- Lifecycle owner: Pass `this` to `.observe()` for automatic cleanup

See [main solution README](../README.md) for more details.