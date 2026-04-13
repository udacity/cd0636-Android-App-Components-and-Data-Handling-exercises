# Kotlin Coroutines Solution

## Overview

This solution demonstrates implementing Kotlin coroutines for async operations in the Task Manager app. It includes viewModelScope usage, proper dispatcher management, parallel execution with async/await, progress tracking with StateFlow, and cancellation handling.

## Implementation Details

### Part 1: Coroutine-Based Task Sync

#### TODO 1.1: Basic Sync with viewModelScope

**TaskViewModel.kt - syncTasks()**

```kotlin
fun syncTasks() {
    viewModelScope.launch {
        _syncState.value = SyncState.Loading
        try {
            val syncedTasks = withContext(Dispatchers.IO) {
                // Simulate network call to sync tasks
                delay(2000)
                fetchTasksFromServer()
            }
            savedStateHandle["tasks"] = syncedTasks
            _syncState.value = SyncState.Success
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
}
```

**Key Points:**
- `viewModelScope.launch` creates a coroutine tied to ViewModel lifecycle
- `withContext(Dispatchers.IO)` switches to IO dispatcher for network work
- Automatic cancellation when ViewModel is cleared
- Error handling with try-catch

#### TODO 1.2: Proper Dispatchers for Different Operations

**TaskViewModel.kt - exportTasks()**

```kotlin
fun exportTasks() {
    viewModelScope.launch {
        val tasks = tasks.value ?: emptyList()

        // Use IO dispatcher for file writing
        withContext(Dispatchers.IO) {
            val jsonString = Json.encodeToString(tasks)
            File(exportPath, "tasks.json").writeText(jsonString)
        }

        // Back to Main dispatcher for UI update
        _exportState.value = ExportState.Success
    }
}
```

**Key Points:**
- IO dispatcher for file operations
- Default (Main) dispatcher for state updates
- Clean separation of concerns

#### TODO 1.3: Parallel Async Operations

**TaskViewModel.kt - syncTasksAndStats()**

```kotlin
fun syncTasksAndStats() {
    viewModelScope.launch {
        try {
            // Run both operations in parallel
            val tasksDeferred = async(Dispatchers.IO) {
                delay(2000)
                fetchTasksFromServer()
            }

            val statsDeferred = async(Dispatchers.IO) {
                delay(1500)
                fetchStatsFromServer()
            }

            // Wait for both to complete
            val tasks = tasksDeferred.await()
            val stats = statsDeferred.await()

            savedStateHandle["tasks"] = tasks
            _syncStats.value = stats
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
}
```

**Key Points:**
- `async` creates a coroutine that returns a result
- Both operations run simultaneously
- `await()` waits for result and suspends if not ready
- Total time = max(2000ms, 1500ms) = 2000ms (vs 3500ms sequential)

### Part 2: Progress Indication and Cancellation

#### TODO 2.1: Progress Updates with StateFlow

**TaskViewModel.kt - StateFlow setup**

```kotlin
private val _syncProgress = MutableStateFlow(0)
val syncProgress: StateFlow<Int> = _syncProgress.asStateFlow()

fun syncTasksWithProgress() {
    viewModelScope.launch {
        try {
            _syncProgress.value = 0

            // Simulate multi-step sync with progress updates
            withContext(Dispatchers.IO) {
                _syncProgress.value = 25  // Fetching tasks
                delay(500)
                val tasks = fetchTasksFromServer()

                _syncProgress.value = 50  // Processing tasks
                delay(500)
                processTasks(tasks)

                _syncProgress.value = 75  // Saving tasks
                delay(500)
                savedStateHandle["tasks"] = tasks

                _syncProgress.value = 100  // Complete
            }
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
}
```

**MainActivity.kt - Collecting progress**

```kotlin
private fun observeSyncProgress() {
    lifecycleScope.launch {
        viewModel.syncProgress.collect { progress ->
            progressBar.progress = progress
            progressBar.visibility = if (progress > 0 && progress < 100) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
```

**Key Points:**
- StateFlow emits current state to new collectors
- Progress updates from background thread propagate to UI
- lifecycleScope automatically cancels when Activity is destroyed

#### TODO 2.2: Cancellation Handling

**TaskViewModel.kt - Cancellable sync**

```kotlin
private var syncJob: Job? = null

fun syncTasks() {
    syncJob = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                repeat(10) { step ->
                    // Check if cancelled
                    ensureActive()

                    delay(500)
                    _syncProgress.value = (step + 1) * 10
                }
            }
            _syncState.value = SyncState.Success
        } catch (e: CancellationException) {
            _syncState.value = SyncState.Cancelled
            throw e  // Re-throw to properly cancel
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
}

fun cancelSync() {
    syncJob?.cancel()
    _syncState.value = SyncState.Cancelled
    _syncProgress.value = 0
}
```

**MainActivity.kt - Cancel button**

```kotlin
cancelButton.setOnClickListener {
    viewModel.cancelSync()
}
```

**Key Points:**
- Store Job reference to enable cancellation
- `ensureActive()` throws CancellationException if cancelled
- Must re-throw CancellationException for proper cooperative cancellation
- Clean up state when cancelled

### UI Integration

**MainActivity.kt - Complete observation setup**

```kotlin
private fun observeSyncState() {
    lifecycleScope.launch {
        viewModel.syncState.collect { state ->
            when (state) {
                is SyncState.Loading -> {
                    statusTextView.text = "Syncing..."
                    syncButton.isEnabled = false
                    cancelButton.isEnabled = true
                }
                is SyncState.Success -> {
                    statusTextView.text = "Sync completed"
                    syncButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
                is SyncState.Error -> {
                    statusTextView.text = "Error: ${state.message}"
                    syncButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
                is SyncState.Cancelled -> {
                    statusTextView.text = "Sync cancelled"
                    syncButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
                is SyncState.Idle -> {
                    statusTextView.text = ""
                    syncButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
            }
        }
    }
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Setup views and adapter
    setupRecyclerView()
    setupClickListeners()

    // Observe ViewModel state
    observeSyncState()
    observeSyncProgress()
}
```

## Architecture Patterns

### ViewModel Scope
- Coroutines launched in viewModelScope survive configuration changes
- Automatically cancelled when ViewModel is cleared
- Perfect for screen-level operations

### Dispatcher Usage
- **Dispatchers.Main** (default): UI updates, StateFlow emissions
- **Dispatchers.IO**: Network calls, file operations, database queries
- **Dispatchers.Default**: CPU-intensive work

### Structured Concurrency
- Parent coroutine waits for all children
- Cancelling parent cancels all children
- Exceptions in children propagate to parent

### StateFlow vs LiveData
- StateFlow is part of Kotlin coroutines, not Android-specific
- Always has a current value
- Better integration with Flow operators
- Requires explicit collection (not lifecycle-aware by default)

## Testing the Solution

1. **Basic Sync**: Click "Sync Tasks" - should show loading state for 2 seconds
2. **Parallel Sync**: Click "Sync with Stats" - faster than sequential (2s vs 3.5s)
3. **Progress**: Click "Sync with Progress" - progress bar fills smoothly
4. **Cancellation**: Click "Sync", then "Cancel" - sync stops immediately
5. **Rotation**: Start sync, rotate device - sync continues without interruption

## Common Issues and Solutions

### Issue: UI freezes during sync
**Solution**: Ensure using Dispatchers.IO for background work, not blocking Main thread

### Issue: Progress bar doesn't update
**Solution**: Check that StateFlow is being collected in lifecycleScope

### Issue: Cancellation doesn't work
**Solution**: Add ensureActive() checks in loops and re-throw CancellationException

### Issue: Coroutines continue after ViewModel destroyed
**Solution**: Use viewModelScope, not GlobalScope

### Issue: State resets on rotation
**Solution**: Use ViewModel to hold state, not Activity variables

## Key Takeaways

1. **viewModelScope** automatically manages coroutine lifecycle
2. **Dispatchers** optimize thread usage for different work types
3. **async/await** enables efficient parallel execution
4. **StateFlow** provides reactive state management
5. **Cancellation** requires cooperative checking with ensureActive()
6. **Error handling** uses try-catch with special CancellationException handling