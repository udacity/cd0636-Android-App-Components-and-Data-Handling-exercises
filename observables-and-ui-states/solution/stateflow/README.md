# StateFlow Solution - Part 2

This folder contains the complete StateFlow implementation for Exercise 5, Part 2.

## What Goes Here

This folder should contain the complete Android project implementing the StateFlow approach:

- **TaskViewModel.kt** - ViewModel using `MutableStateFlow` and `StateFlow`
- **MainActivity.kt** - Activity collecting StateFlow with coroutines
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
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    // Computed properties using stateIn
    val totalTaskCountFlow: StateFlow<Int> = _tasksFlow
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTaskCountFlow: StateFlow<Int> = _tasksFlow
        .map { it.count { it.completed } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        _tasksFlow.value = listOf(/* sample tasks */)
    }

    fun addTask(title: String) { /* update _tasksFlow.value */ }
    fun toggleTaskCompletion(taskId: Int) { /* update _tasksFlow.value */ }
    fun deleteTask(taskId: Int) { /* update _tasksFlow.value */ }
}
```

### MainActivity.kt
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... setup ...
        observeViewModelWithFlow()
    }

    private fun observeViewModelWithFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // IMPORTANT: Separate launch blocks for parallel collection
                launch {
                    viewModel.tasksFlow.collect { tasks ->
                        taskAdapter.updateTasks(tasks)
                    }
                }

                launch {
                    viewModel.totalTaskCountFlow.collect { count ->
                        totalTasksTextView.text = "Total: $count"
                    }
                }

                launch {
                    viewModel.completedTaskCountFlow.collect { count ->
                        completedTasksTextView.text = "Completed: $count"
                    }
                }
            }
        }
    }
}
```

## Key Concepts

**StateFlow Benefits:**
- Platform-independent (Kotlin multiplatform)
- No nullability (requires initial value)
- Synchronous value access
- Works seamlessly with coroutines

**Important Patterns:**
- Encapsulation: Private `MutableStateFlow`, public `StateFlow` with `.asStateFlow()`
- Reactivity: Set `.value` to emit new values
- Transformations: Use `.map().stateIn()` for computed properties
- Lifecycle management: Use `repeatOnLifecycle` for automatic lifecycle awareness
- **Critical:** Use separate `launch` blocks for each `.collect()` to run them in parallel

**Common Mistake:**
```kotlin
// WRONG - only first collect runs
repeatOnLifecycle(Lifecycle.State.STARTED) {
    flow1.collect { }
    flow2.collect { }  // Never reached!
}

// RIGHT - both run in parallel
repeatOnLifecycle(Lifecycle.State.STARTED) {
    launch { flow1.collect { } }
    launch { flow2.collect { } }
}
```

See [main solution README](../README.md) for more details and comparison with LiveData.