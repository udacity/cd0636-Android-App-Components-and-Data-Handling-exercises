# Task Manager with Room CRUD Operations - Solution

## Overview

This solution demonstrates implementing complete CRUD (Create, Read, Update, Delete) operations
using Room database. It builds on the database infrastructure from the previous lesson by adding
query methods to the DAO, wiring them through a Repository layer, connecting to the ViewModel, and
integrating with the UI for reactive updates via Kotlin Flow.

## Implementation Guide

This guide provides detailed step-by-step instructions with complete code for each TODO.

### Part 1: Implement Room Operations

#### TODO 1.1: Add insertTask() in TaskDao

**File:** `TaskDao.kt`

Add the insert operation with conflict strategy:

```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertTask(task: TaskEntity)
```

**Key Points:**

- `@Insert` generates the INSERT SQL automatically
- `OnConflictStrategy.REPLACE` updates existing rows with the same primary key
- `suspend` makes this a coroutine function for background execution

---

#### TODO 1.2: Add updateTask() in TaskDao

Add the update operation:

```kotlin
@Update
suspend fun updateTask(task: TaskEntity)
```

**Key Points:**

- `@Update` generates UPDATE SQL matching by primary key
- Room updates all columns of the entity
- `suspend` ensures this runs off the main thread

---

#### TODO 1.3: Add deleteTaskById() in TaskDao

Add the delete operation using a custom query:

```kotlin
@Query("DELETE FROM tasks WHERE id = :taskId")
suspend fun deleteTaskById(taskId: Int)
```

**Key Points:**

- `@Query` allows custom SQL when convenience annotations aren't sufficient
- `:taskId` binds the function parameter to the SQL query
- Room validates the SQL at compile time

---

#### Complete TaskDao

**File:** `TaskDao.kt`

```kotlin
package com.udacity.project.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Task database operations.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}
```

**Key Points:**

- `getAllTasks()` returns `Flow<List<TaskEntity>>` for reactive updates
- Flow automatically re-emits when the underlying data changes
- Read operations return Flow (continuous stream); write operations are suspend (one-shot)
- `ORDER BY created_at DESC` shows newest tasks first

---

#### TODO 1.4: Implement addTask() in TaskRepository

**File:** `TaskRepository.kt`

```kotlin
suspend fun addTask(task: Task) {
    taskDao.insertTask(task.toEntity())
}
```

---

#### TODO 1.5: Implement updateTask() in TaskRepository

```kotlin
suspend fun updateTask(task: Task) {
    taskDao.updateTask(task.toEntity())
}
```

---

#### TODO 1.6: Implement deleteTask() in TaskRepository

```kotlin
suspend fun deleteTask(taskId: Int) {
    taskDao.deleteTaskById(taskId)
}
```

---

#### Complete TaskRepository

**File:** `TaskRepository.kt`

```kotlin
package com.udacity.project.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing task data.
 * Acts as a single source of truth for task data.
 */
class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
}
```

**Key Points:**

- Repository accepts `TaskDao` via constructor injection (testable)
- `getAllTasks()` maps `Flow<List<TaskEntity>>` to `Flow<List<Task>>` (entity → domain)
- Write methods convert domain models to entities before calling DAO
- Repository shields the ViewModel from database implementation details

---

### Part 2: Wire Up ViewModel

#### TODO 2.1: Implement addTask()

**File:** `TaskViewModel.kt`

```kotlin
fun addTask(title: String) {
    viewModelScope.launch {
        val task = Task(
            id = System.currentTimeMillis().toInt(),
            title = title,
            completed = false,
            isFavorite = false
        )
        repository.addTask(task)
    }
}
```

**Key Points:**

- `viewModelScope.launch` starts a coroutine tied to the ViewModel lifecycle
- `System.currentTimeMillis().toInt()` generates a unique ID
- Coroutine is automatically cancelled when ViewModel is cleared

---

#### TODO 2.2: Implement toggleTaskCompletion()

```kotlin
fun toggleTaskCompletion(taskId: Int) {
    viewModelScope.launch {
        val currentTasks = tasks.value
        val task = currentTasks.find { it.id == taskId }
        task?.let {
            repository.updateTask(it.copy(completed = !it.completed))
        }
    }
}
```

**Key Points:**

- Finds the task in the current list by ID
- Uses `copy()` to create a new instance with toggled completion
- `let` ensures null safety if task is not found

---

#### TODO 2.3: Implement deleteTask()

```kotlin
fun deleteTask(taskId: Int) {
    viewModelScope.launch {
        repository.deleteTask(taskId)
    }
}
```

---

#### Complete TaskViewModel

**File:** `TaskViewModel.kt`

```kotlin
package com.udacity.project.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing task list with Room database.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TaskDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())

    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Log.d("TaskViewModel", "ViewModel initialized")
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            val task = Task(
                id = System.currentTimeMillis().toInt(),
                title = title,
                completed = false,
                isFavorite = false
            )
            repository.addTask(task)
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            val currentTasks = tasks.value
            val task = currentTasks.find { it.id == taskId }
            task?.let {
                repository.updateTask(it.copy(completed = !it.completed))
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TaskViewModel", "ViewModel cleared")
    }
}
```

**Key Points:**

- Extends `AndroidViewModel` to access `Application` context for database initialization
- `stateIn()` converts Flow to StateFlow for UI consumption
- `SharingStarted.WhileSubscribed(5000)` keeps the flow active for 5 seconds after last subscriber
- Empty list as initial value prevents null states

---

### Part 3: Connect MainActivity

#### TODO 3.1 - 3.4: Wire Up UI Callbacks

**File:** `MainActivity.kt`

```kotlin
package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
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

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskToggled = { taskId ->
                // TODO 3.1: Wire up toggle callback
                viewModel.toggleTaskCompletion(taskId)
            },
            onTaskDeleted = { taskId ->
                // TODO 3.2: Wire up delete callback
                viewModel.deleteTask(taskId)
            }
        )
        tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun setupClickListeners() {
        addTaskButton.setOnClickListener {
            val title = taskInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                // TODO 3.3: Wire up add button
                viewModel.addTask(title)
                // TODO 3.4: Clear input field
                taskInputEditText.text.clear()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { tasks ->
                    taskAdapter.updateTasks(tasks)
                    totalTasksTextView.text = "Total: ${tasks.size}"
                    completedTasksTextView.text =
                        "Completed: ${tasks.count { it.completed }}"
                }
            }
        }
    }

    // ... initializeViews() omitted for brevity
}
```

**Key Points:**

- `repeatOnLifecycle(Lifecycle.State.STARTED)` collects flow only when activity is visible
- Toggle callback calls `viewModel.toggleTaskCompletion()` with the task ID
- Delete callback calls `viewModel.deleteTask()` with the task ID
- Add button validates non-empty input, calls `addTask()`, then clears the field
- Flow collection automatically updates UI when database changes

---

## Architecture Diagram

```
┌─────────────┐
│ MainActivity │
│              │
│ - collect    │
│   tasks flow │
│ - UI events  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ TaskViewModel │
│              │
│ - tasks      │ ← StateFlow<List<Task>>
│ - addTask()  │
│ - toggle()   │
│ - delete()   │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│  TaskRepository   │
│                  │
│ - getAllTasks()   │ → Flow<List<Task>>
│ - addTask()      │
│ - updateTask()   │
│ - deleteTask()   │
└──────┬───────────┘
       │ Entity ↔ Domain mapping
       ▼
┌──────────────────┐
│    TaskDao        │
│                  │
│ - getAllTasks()   │ → Flow<List<TaskEntity>>
│ - insertTask()   │ suspend
│ - updateTask()   │ suspend
│ - deleteTaskById()│ suspend
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  TaskDatabase     │
│  (Room/SQLite)   │
└──────────────────┘
```

## Data Flow

### Read Flow (Reactive)

1. Room executes `SELECT * FROM tasks` query
2. Returns `Flow<List<TaskEntity>>` that auto-updates on changes
3. Repository maps entities to domain models
4. ViewModel exposes as `StateFlow<List<Task>>`
5. UI collects and renders the list

### Write Flow (One-Shot)

1. UI triggers action (add, toggle, delete)
2. ViewModel launches coroutine in `viewModelScope`
3. Repository converts domain model → entity (if needed)
4. DAO executes the suspend function
5. Room's Flow automatically re-emits updated data
6. UI updates reactively — no manual refresh needed

## Testing the Implementation

### Test Case 1: Add a Task

1. Type a task title in the input field
2. Tap the "Add" button
3. Task appears in the list
4. Input field is cleared
5. Task count updates

### Test Case 2: Toggle Task Completion

1. Tap a task's checkbox
2. Completed status toggles
3. Completed count updates
4. Change persists after rotation

### Test Case 3: Delete a Task

1. Tap a task's delete button
2. Task is removed from the list
3. Total count decreases
4. Deletion persists after app restart

### Test Case 4: Data Persistence

1. Add several tasks
2. Force-close the app
3. Reopen the app
4. All tasks are still present (stored in Room database)

### Test Case 5: Reactive Updates

1. Add a task
2. List updates automatically without manual refresh
3. No `notifyDataSetChanged()` trigger needed from ViewModel

## Common Issues and Solutions

### Issue 1: Cannot Access Database on Main Thread

**Error:** `Cannot access database on the main thread`

**Solution:** Use `suspend` functions and call them from `viewModelScope.launch`:

```kotlin
viewModelScope.launch {
    repository.addTask(task)
}
```

---

### Issue 2: Flow Not Emitting Updates

**Error:** UI doesn't update after database changes

**Solution:** Ensure you're collecting with lifecycle awareness:

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.tasks.collect { /* update UI */ }
    }
}
```

---

### Issue 3: OnConflictStrategy Not Working

**Error:** Duplicate entries or crashes on insert

**Solution:** Ensure `@Insert(onConflict = OnConflictStrategy.REPLACE)` is set and the primary key
matches.

---

## Summary

This implementation demonstrates:

- ✅ Complete CRUD operations in Room DAO
- ✅ Repository pattern as single source of truth
- ✅ Domain model separation from database entities
- ✅ Reactive data flow with Kotlin Flow and StateFlow
- ✅ Coroutine-based write operations with viewModelScope
- ✅ Lifecycle-aware UI collection with repeatOnLifecycle
- ✅ Full offline functionality with persistent storage

The app now has a complete data layer that persists tasks across app restarts and provides reactive
UI updates whenever data changes.
