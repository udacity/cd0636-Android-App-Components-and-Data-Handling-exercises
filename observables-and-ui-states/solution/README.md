# Task Manager - Observables and UI State Solutions

This folder contains solutions for Exercise 5, demonstrating reactive UI updates with both LiveData
and StateFlow approaches.

- **[LiveData Solution](./livedata)** - Android's lifecycle-aware observable pattern
- **[StateFlow Solution](./stateflow)** - Kotlin's coroutine-based reactive pattern

Both solutions achieve the same functionality using different technologies. See the individual
solution folders for complete implementations.

---

## Quick Reference

### LiveData Approach

**ViewModel (Part 1.1 - TODOs 1.1.1, 1.1.2, 1.1.3):**

```kotlin
class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    val totalTaskCount: LiveData<Int> = _tasks.map { it.size }
    val completedTaskCount: LiveData<Int> = _tasks.map { it.count { task -> task.completed } }

    init {
        _tasks.value = listOf(/* initial tasks */)
    }

    fun addTask(title: String) {
        val updatedTasks = (_tasks.value ?: emptyList()) + Task(nextTaskId++, title, false)
        _tasks.value = updatedTasks
    }

    // Similar for toggleTaskCompletion() and deleteTask()
}
```

**Activity (Part 1.2 - TODOs 1.2.1, 1.2.2, 1.2.3):**

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

**Key points:**
- Use `MutableLiveData` privately, expose `LiveData` publicly
- Set `.value` to trigger observers
- Use `.map()` for computed properties
- Use `.observe(this) { }` with lifecycle owner

---

### StateFlow Approach

**ViewModel (Part 2.1 - TODOs 2.1.1, 2.1.2, 2.1.3):**

```kotlin
class TaskViewModel : ViewModel() {
    private val _tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksFlow: StateFlow<List<Task>> = _tasksFlow.asStateFlow()

    val totalTaskCountFlow: StateFlow<Int> = _tasksFlow
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTaskCountFlow: StateFlow<Int> = _tasksFlow
        .map { it.count { task -> task.completed } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        _tasksFlow.value = listOf(/* initial tasks */)
    }

    fun addTask(title: String) {
        val updatedTasks = _tasksFlow.value + Task(nextTaskId++, title, false)
        _tasksFlow.value = updatedTasks
    }

    // Similar for toggleTaskCompletion() and deleteTask()
}
```

**Activity (Part 2.2 - TODOs 2.2.1, 2.2.2, 2.2.3):**

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // ... setup ...
        observeViewModelWithFlow()
    }

    private fun observeViewModelWithFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Separate launch blocks for parallel collection
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

**Key points:**
- StateFlow requires initial value (not nullable)
- Use `.asStateFlow()` for read-only exposure
- Use `.map().stateIn()` for computed properties
- Use separate `launch` blocks for each `.collect()`
- Use `repeatOnLifecycle` for lifecycle awareness

---

## Comparison Summary

| Feature | LiveData | StateFlow |
|---------|----------|-----------|
| **Initial value** | Optional | Required |
| **Platform** | Android | Kotlin multiplatform |
| **Lifecycle** | Automatic | Manual with `repeatOnLifecycle` |
| **Observation** | `.observe(this) { }` | `.collect { }` in coroutine |
| **Computed values** | `.map { }` | `.map { }.stateIn(...)` |
| **Parallel observation** | Multiple `.observe()` calls | Separate `launch` blocks |