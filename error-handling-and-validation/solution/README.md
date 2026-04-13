# Task Manager with Error Handling - Solution

## Overview

This solution demonstrates implementing error handling for network operations using sealed classes.
It covers modeling error states with `NetworkResult` and `NetworkError` sealed class hierarchies,
managing sync state in the ViewModel, displaying errors with appropriate icons and actionable
guidance, and providing retry functionality. This pattern ensures every error case is handled and
the user always receives clear feedback.

## Implementation Guide

This guide provides detailed step-by-step instructions with complete code for each TODO.

### Part 1: ViewModel Error Handling

#### TODO Step 1.1: Add Error State LiveData

**File:** `TaskViewModel.kt`

Create LiveData to track the current sync state:

```kotlin
// Sync state management
private val _syncState = MutableLiveData<NetworkResult<Unit>>()
val syncState: LiveData<NetworkResult<Unit>> = _syncState

private var lastSyncAttempt: Long = 0
private val minSyncInterval = 30_000L // 30 seconds
```

**Key Points:**

- Private `MutableLiveData` for internal updates, public `LiveData` for UI observation
- `NetworkResult<Unit>` represents three states: Loading, Success, and Error
- `lastSyncAttempt` tracks timing to prevent excessive retry attempts
- `minSyncInterval` adds rate limiting (30 seconds between syncs)

---

#### TODO Step 1.2: Implement syncTasks() Function

Add the sync function with state management:

```kotlin
/**
 * Syncs tasks with simulated network operation and error handling.
 */
fun syncTasks() {
    // Prevent too frequent sync attempts
    val now = System.currentTimeMillis()
    if (now - lastSyncAttempt < minSyncInterval) {
        _syncState.value = NetworkResult.Error(
            NetworkError.Unknown("Please wait before trying again")
        )
        return
    }
    lastSyncAttempt = now

    viewModelScope.launch {
        _syncState.value = NetworkResult.Loading

        // Simulate network delay
        delay(1500)

        // Simulate different error scenarios
        val result = simulateNetworkSync()

        _syncState.value = when (result) {
            is NetworkResult.Success -> {
                Log.d("TaskViewModel", "Sync successful")
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> {
                Log.e("TaskViewModel", "Sync failed: ${result.error.message}")
                result
            }
            NetworkResult.Loading -> NetworkResult.Loading
        }
    }
}
```

**Key Points:**

- Rate limiting prevents rapid-fire sync attempts
- `NetworkResult.Loading` is set immediately to show progress indicator
- Coroutine runs in `viewModelScope` — automatically cancelled if ViewModel is cleared
- Result is logged for debugging, then posted to LiveData for UI

---

#### TODO Step 1.3: Implement simulateNetworkSync() Function

Create a simulation that randomly returns different error types:

```kotlin
/**
 * Simulates network sync with various error scenarios.
 * In production, this would call a real repository method.
 */
private suspend fun simulateNetworkSync(): NetworkResult<Unit> {
    return when (Random.nextInt(6)) {
        0 -> NetworkResult.Error(NetworkError.NoInternet())
        1 -> NetworkResult.Error(NetworkError.Timeout())
        2 -> NetworkResult.Error(NetworkError.ServerError(500))
        3 -> NetworkResult.Error(NetworkError.NotFound())
        4 -> NetworkResult.Error(NetworkError.Unknown("Unexpected error occurred"))
        else -> NetworkResult.Success(Unit)
    }
}
```

**Add helper methods for retry logic:**

```kotlin
fun canRetrySync(): Boolean {
    val now = System.currentTimeMillis()
    return now - lastSyncAttempt >= minSyncInterval
}

fun resetSyncState() {
    _syncState.value = null
}
```

**Key Points:**

- Random simulation demonstrates all five error types for testing
- In production, map real exceptions to `NetworkError` types (e.g., `IOException` → `NoInternet`)
- `canRetrySync()` checks if enough time has passed for another attempt
- `resetSyncState()` clears the error state for a fresh start

---

#### NetworkResult and NetworkError Sealed Classes

**File:** `NetworkResult.kt`

```kotlin
package com.udacity.project.app

/**
 * Sealed class representing the result of a network operation.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

/**
 * Sealed class hierarchy for different types of network errors.
 */
sealed class NetworkError(open val message: String) {
    data class NoInternet(
        override val message: String = "No internet connection"
    ) : NetworkError(message)

    data class Timeout(
        override val message: String = "Request timed out"
    ) : NetworkError(message)

    data class ServerError(
        val code: Int,
        override val message: String = "Server error: $code"
    ) : NetworkError(message)

    data class NotFound(
        override val message: String = "Resource not found"
    ) : NetworkError(message)

    data class Unknown(
        override val message: String = "An unexpected error occurred"
    ) : NetworkError(message)
}
```

**Key Points:**

- `NetworkResult<out T>` is generic — works with any data type
- Three states: `Loading`, `Success(data)`, `Error(error)`
- `NetworkError` hierarchy models five distinct error types
- Each error type has a default user-friendly message
- `ServerError` carries the HTTP status code for additional context
- `sealed` ensures the compiler checks all cases in `when` expressions

---

### Part 2: UI Error Display

#### TODO Step 2.1: Observe Sync State

**File:** `MainActivity.kt`

Observe `viewModel.syncState` and handle each state:

```kotlin
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

    // Observe sync state
    viewModel.syncState.observe(this) { state ->
        when (state) {
            is NetworkResult.Loading -> {
                loadingProgress.visibility = View.VISIBLE
                errorContainer.visibility = View.GONE
                syncButton.isEnabled = false
                tasksRecyclerView.visibility = View.VISIBLE
            }
            is NetworkResult.Success -> {
                loadingProgress.visibility = View.GONE
                errorContainer.visibility = View.GONE
                syncButton.isEnabled = true
                tasksRecyclerView.visibility = View.VISIBLE

                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Tasks synced successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            is NetworkResult.Error -> {
                loadingProgress.visibility = View.GONE
                syncButton.isEnabled = viewModel.canRetrySync()

                showErrorState(state.error)
            }
            null -> {
                loadingProgress.visibility = View.GONE
                errorContainer.visibility = View.GONE
                syncButton.isEnabled = true
                tasksRecyclerView.visibility = View.VISIBLE
            }
        }
    }
}
```

**Key Points:**

- `Loading`: Show progress bar, disable sync button to prevent duplicate requests
- `Success`: Hide loading, show success Snackbar, re-enable button
- `Error`: Hide loading, display error UI, conditionally enable retry
- `null`: Initial state — reset everything to defaults
- Sync button disabled during loading and when retry interval hasn't elapsed

---

#### TODO Step 2.2: Implement showErrorState() Function

Create a function that displays error details with visual cues:

```kotlin
private fun showErrorState(error: NetworkError) {
    val shouldShowFullError = error is NetworkError.NoInternet ||
            error is NetworkError.ServerError

    if (shouldShowFullError) {
        errorContainer.visibility = View.VISIBLE
        tasksRecyclerView.visibility = View.GONE

        // Set error icon based on error type
        val iconRes = when (error) {
            is NetworkError.NoInternet -> android.R.drawable.ic_dialog_alert
            is NetworkError.Timeout -> android.R.drawable.ic_dialog_info
            is NetworkError.ServerError -> android.R.drawable.ic_dialog_alert
            is NetworkError.NotFound -> android.R.drawable.ic_menu_search
            is NetworkError.Unknown -> android.R.drawable.ic_dialog_alert
        }
        errorIcon.setImageResource(iconRes)
        errorIcon.setColorFilter(
            ContextCompat.getColor(this, android.R.color.holo_red_dark)
        )

        // Set error message
        errorMessage.text = error.message

        // Set actionable guidance
        val subtitle = when (error) {
            is NetworkError.NoInternet ->
                "Check your internet connection and try again"
            is NetworkError.Timeout ->
                "The request took too long. Try again later"
            is NetworkError.ServerError ->
                "Our servers are having issues. Try again later"
            is NetworkError.NotFound ->
                "The requested data could not be found"
            is NetworkError.Unknown ->
                "Something went wrong. Please try again"
        }
        errorSubtitle.text = subtitle

        retryButton.isEnabled = viewModel.canRetrySync()
    } else {
        // Less critical errors shown as Snackbar
        errorContainer.visibility = View.GONE
        tasksRecyclerView.visibility = View.VISIBLE

        Snackbar.make(
            findViewById(android.R.id.content),
            error.message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            if (viewModel.canRetrySync()) {
                viewModel.syncTasks()
            }
        }.show()
    }
}
```

**Key Points:**

- Critical errors (NoInternet, ServerError) show full-screen error state
- Less critical errors (Timeout, NotFound, Unknown) show Snackbar with retry action
- Each error type gets a specific icon for visual identification
- Actionable subtitle tells users what to do (not just what went wrong)
- Retry button respects rate limiting via `canRetrySync()`
- `when` is exhaustive — compiler ensures every error type is handled

---

## Error Handling Flow

```
User taps "Sync"
        │
        ▼
viewModel.syncTasks()
        │
        ├── Rate limited? → Show "Please wait" error
        │
        └── OK to sync
              │
              ▼
        Set Loading state
        UI: Show progress, disable button
              │
              ▼
        Network operation
              │
        ┌─────┴─────┐
        │            │
    Success       Error
        │            │
        ▼            ▼
    Snackbar     Determine severity
    "Synced!"         │
                ┌─────┴─────┐
                │            │
           Critical      Minor
           (NoInternet,  (Timeout,
            ServerError)  NotFound,
                │         Unknown)
                ▼            │
          Full error         ▼
          container       Snackbar
          with icon,      with retry
          message,        action
          subtitle,
          retry button
```

## Testing the Implementation

### Test Case 1: Loading State

1. Tap the "Sync" button
2. Progress bar appears
3. Sync button becomes disabled
4. After 1.5 seconds, result appears

### Test Case 2: Success State

1. Sync completes successfully (random)
2. Progress bar disappears
3. Snackbar shows "Tasks synced successfully"
4. Sync button re-enables

### Test Case 3: NoInternet Error

1. Sync returns NoInternet error (random)
2. Full error container appears with alert icon
3. Message: "No internet connection"
4. Subtitle: "Check your internet connection and try again"
5. Retry button available

### Test Case 4: ServerError

1. Sync returns ServerError (random)
2. Full error container with alert icon
3. Message: "Server error: 500"
4. Subtitle: "Our servers are having issues. Try again later"

### Test Case 5: Timeout/NotFound/Unknown Errors

1. Sync returns a minor error (random)
2. Snackbar appears with error message
3. "Retry" action available on Snackbar
4. Task list remains visible

### Test Case 6: Rate Limiting

1. Tap sync, wait for result
2. Immediately tap sync again
3. Error: "Please wait before trying again"
4. Wait 30 seconds, sync works again

### Test Case 7: Retry Functionality

1. Get an error state
2. Tap retry button or Snackbar action
3. Sync attempt begins again
4. May succeed or fail randomly

## Common Issues and Solutions

### Issue 1: Exhaustive When Not Enforced

**Problem:** Adding a new `NetworkError` subclass doesn't cause compile errors.

**Solution:** Use `when` as an expression (assign result) instead of a statement:

```kotlin
// Statement — no exhaustive check
when (error) {
        ...
}

// Expression — compiler enforces all cases
val message = when (error) {
        ...
}
```

---

### Issue 2: LiveData Null Initial Value

**Problem:** `syncState` emits null on first observation.

**Solution:** Handle `null` in the observer as the initial/reset state:

```kotlin
null -> {
    // Reset to initial state
    loadingProgress.visibility = View.GONE
    errorContainer.visibility = View.GONE
}
```

---

### Issue 3: Retry Button Still Enabled

**Problem:** Users can spam retry, overwhelming the server.

**Solution:** Rate limit with `canRetrySync()` and disable button accordingly:

```kotlin
retryButton.isEnabled = viewModel.canRetrySync()
```

---

## Summary

This implementation demonstrates:

- ✅ `NetworkResult` sealed class with Loading, Success, and Error states
- ✅ `NetworkError` sealed class hierarchy with five specific error types
- ✅ ViewModel sync state management with LiveData
- ✅ Rate limiting to prevent excessive sync attempts
- ✅ Full-screen error display for critical errors (NoInternet, ServerError)
- ✅ Snackbar display for minor errors (Timeout, NotFound, Unknown)
- ✅ Error-specific icons and actionable guidance messages
- ✅ Retry functionality with rate limiting
- ✅ Exhaustive error handling enforced by sealed classes

The error handling pattern is production-ready, providing clear user feedback for every failure
scenario while preventing misuse through rate limiting.
