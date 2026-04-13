# Exercise 30: Error Handling - Starter

## Overview

In this exercise, you'll implement error handling for sync operations in an Android app. You'll learn how to handle different types of errors using sealed classes, display errors appropriately in the UI, and provide retry functionality.

## Learning Objectives

- Use sealed classes for type-safe error representation (`NetworkResult`, `NetworkError`)
- Handle different error scenarios (no internet, timeout, server errors, etc.)
- Display errors with appropriate icons and messages
- Provide actionable feedback and retry functionality

## What's Provided

- **NetworkResult.kt**: Complete sealed class for representing operation results (Success, Error, Loading)
- **NetworkError.kt**: Complete sealed class for different error types (NoInternet, Timeout, ServerError, NotFound, Unknown)
- **activity_main.xml**: Complete layout with error state UI (error container, loading progress, sync button)
- **Task.kt, TaskAdapter.kt**: Complete task management components

## Your Tasks

### Part 1: TaskViewModel - Error Handling

Open `TaskViewModel.kt` and complete the following:

#### Step 1.1: Add Sync State Management

Create LiveData for tracking sync state:
```kotlin
private val _syncState = MutableLiveData<NetworkResult<Unit>?>()
val syncState: LiveData<NetworkResult<Unit>?> = _syncState
```

**Why:** This allows the UI to observe and react to sync state changes.

#### Step 1.2: Implement syncTasks() Function

Create the main sync function that:
1. Launch a coroutine in `viewModelScope`
2. Set state to `NetworkResult.Loading`
3. Add `delay(1500)` to simulate network latency
4. Call `simulateNetworkSync()` and update state with the result
5. Add logging for debugging

**Why:** This coordinates the sync operation and manages state updates.

#### Step 1.3: Implement simulateNetworkSync()

Create a private suspend function that returns `NetworkResult<Unit>`. Use `Random.nextInt(6)` to simulate different scenarios:
- 0: `NetworkResult.Error(NetworkError.NoInternet())`
- 1: `NetworkResult.Error(NetworkError.Timeout())`
- 2: `NetworkResult.Error(NetworkError.ServerError(500))`
- 3: `NetworkResult.Error(NetworkError.NotFound())`
- 4: `NetworkResult.Error(NetworkError.Unknown("Unexpected error"))`
- else: `NetworkResult.Success(Unit)`

**Why:** Simulates real-world network failures for testing error handling.

### Part 2: MainActivity - Error Display UI

Open `MainActivity.kt` and complete the following:

#### Step 2.1: Observe Sync State

In the `observeViewModel()` function, add an observer for `viewModel.syncState` that handles all states:

**NetworkResult.Loading:**
- Show `loadingProgress` (set visibility to VISIBLE)
- Hide `errorContainer` (set visibility to GONE)
- Disable `syncButton` (isEnabled = false)
- Keep `tasksRecyclerView` visible

**NetworkResult.Success:**
- Hide `loadingProgress`
- Hide `errorContainer`
- Enable `syncButton`
- Show success Snackbar: "Tasks synced successfully"

**NetworkResult.Error:**
- Hide `loadingProgress`
- Enable `syncButton`
- Call `showErrorState(state.error)` to display the error

**null (initial state):**
- Reset all UI to default state (hide loading, hide error, enable button)

#### Step 2.2: Implement showErrorState() Function

Create a private function that takes a `NetworkError` parameter and displays it:

1. **Show error container:** Set `errorContainer.visibility = View.VISIBLE` and hide RecyclerView
2. **Set error icon** based on error type:
   - NoInternet: `android.R.drawable.ic_dialog_alert`
   - Timeout: `android.R.drawable.ic_dialog_info`
   - ServerError: `android.R.drawable.ic_dialog_alert`
   - NotFound: `android.R.drawable.ic_menu_search`
   - Unknown: `android.R.drawable.ic_dialog_alert`
3. **Apply red color filter:** `errorIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))`
4. **Set error message:** `errorMessage.text = error.message`
5. **Set actionable subtitle:**
   - NoInternet: "Check your internet connection and try again"
   - Timeout: "The request took too long. Try again later"
   - ServerError: "Our servers are having issues. Try again later"
   - NotFound: "The requested data could not be found"
   - Unknown: "Something went wrong. Please try again"
6. **Enable retry button:** `retryButton.isEnabled = true`

## Testing Your Implementation

1. **Run the app** and tap the "Sync Tasks" button
2. **Observe different error scenarios** (they're randomly generated)
3. **Verify error UI:**
   - Error container shows with appropriate icon
   - Error message displays correctly
   - Actionable subtitle provides guidance
4. **Test retry functionality:** Tap retry button to attempt sync again
5. **Verify success state:** Shows success Snackbar when sync succeeds

## Key Concepts

### Sealed Classes for Error Handling

Sealed classes provide type-safe way to represent different states:
```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
```

**Benefits:**
- Compiler ensures all cases are handled in `when` expressions
- Type-safe - can't accidentally miss a state
- Can carry associated data (error messages, codes)

### User-Friendly Error Messages

Always provide:
1. **Clear description:** What went wrong
2. **Actionable guidance:** What the user should do
3. **Visual cues:** Icons that match the error type
4. **Retry option:** Let users try again

## Expected Behavior

When you complete this exercise:
- Tapping "Sync Tasks" triggers a simulated network operation
- Loading state shows a progress indicator
- Success shows a brief success message
- Errors are displayed with appropriate icons and messages
- Retry button allows users to try again
- Each error type has a unique icon and helpful message

## Common Mistakes to Avoid

1. **Forgetting null checks:** Always check if `error` is the right type before accessing properties
2. **Using wrong visibility constants:** Use `View.VISIBLE`, `View.GONE` (not `View.INVISIBLE`)
3. **Forgetting to hide/show opposing views:** When showing error container, hide RecyclerView (and vice versa)
4. **Not using when expression:** Use `when` with sealed classes for exhaustive handling
5. **Missing observer lifecycle:** Use `viewLifecycleOwner` in fragments, `this` in activities

## Solution Reference

Check the `solution` folder to see a complete implementation if you get stuck.