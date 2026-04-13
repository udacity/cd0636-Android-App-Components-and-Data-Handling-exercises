# Task Manager with Form Validation - Solution

## Overview

This solution demonstrates implementing form validation in an Android application with real-time
feedback. It covers creating validation rules in the ViewModel using a sealed class, connecting
validation to the UI with TextInputLayout for error display, debounced async validation for
duplicate checking, and Material Design components for a polished user experience.

## Implementation Guide

This guide provides detailed step-by-step instructions with complete code for each TODO.

### Part 1: Validation Logic in ViewModel

#### TODO Step 1: Implement Validation Rules

**File:** `TaskViewModel.kt`

Create the `validateTaskTitle()` method with validation rules:

```kotlin
companion object {
    private const val MIN_TITLE_LENGTH = 3
    private const val MAX_TITLE_LENGTH = 100
}

/**
 * Validates task title against basic rules.
 */
fun validateTaskTitle(title: String): ValidationResult {
    return when {
        title.isBlank() -> ValidationResult.Invalid("Task title cannot be empty")
        title.length < MIN_TITLE_LENGTH -> ValidationResult.Invalid(
            "Task title must be at least $MIN_TITLE_LENGTH characters"
        )
        title.length > MAX_TITLE_LENGTH -> ValidationResult.Invalid(
            "Task title must not exceed $MAX_TITLE_LENGTH characters"
        )
        else -> ValidationResult.Valid
    }
}
```

**Key Points:**

- `when` expression checks rules in order — first failing rule wins
- `isBlank()` catches empty strings and whitespace-only input
- Constants make validation rules easy to adjust
- Returns `ValidationResult` sealed class for type-safe error handling

**Add async validation with debouncing for duplicate detection:**

```kotlin
private var validateJob: Job? = null

/**
 * Validates task title asynchronously with debouncing and duplicate checking.
 */
fun validateTaskTitleAsync(title: String) {
    validateJob?.cancel()

    // Immediate validation for basic rules
    val basicValidation = validateTaskTitle(title)
    if (basicValidation is ValidationResult.Invalid) {
        _taskTitleValidation.value = basicValidation
        return
    }

    // Async validation for duplicates with debouncing
    validateJob = viewModelScope.launch {
        delay(300) // Debounce user input

        val isDuplicate = isTaskTitleDuplicate(title)

        _taskTitleValidation.value = if (isDuplicate) {
            ValidationResult.Invalid("A task with this title already exists")
        } else {
            ValidationResult.Valid
        }
    }
}

private fun isTaskTitleDuplicate(title: String): Boolean {
    val currentTasks = tasks.value ?: emptyList()
    return currentTasks.any { it.title.equals(title, ignoreCase = true) }
}
```

**Key Points:**

- `validateJob?.cancel()` cancels previous validation to prevent stale results
- Basic rules run immediately (no delay for obvious errors)
- Duplicate check is debounced by 300ms to avoid checking on every keystroke
- Case-insensitive comparison prevents "Buy milk" and "buy milk" duplicates

---

#### TODO Step 2: Validate Before Saving

**File:** `TaskViewModel.kt`

Update the `addTask()` method to validate input first:

```kotlin
/**
 * Adds a new task with validation.
 */
fun addTask(title: String, onSuccess: () -> Unit, onError: () -> Unit) {
    val validation = validateTaskTitle(title)

    if (validation is ValidationResult.Invalid) {
        _taskTitleValidation.value = validation
        onError()
        return
    }

    val isDuplicate = isTaskTitleDuplicate(title)

    if (isDuplicate) {
        _taskTitleValidation.value = ValidationResult.Invalid(
            "A task with this title already exists"
        )
        onError()
    } else {
        val currentTasks = tasks.value ?: emptyList()
        val newTask = Task(
            id = nextTaskId++,
            title = title.trim(),
            completed = false
        )
        val updatedTasks = currentTasks + newTask
        savedStateHandle["tasks"] = updatedTasks
        _taskTitleValidation.value = ValidationResult.Valid
        onSuccess()
    }
}

fun resetValidation() {
    _taskTitleValidation.value = ValidationResult.Valid
}
```

**Key Points:**

- Double validation: basic rules + duplicate check before saving
- `title.trim()` removes leading/trailing whitespace before storage
- Callbacks (`onSuccess`, `onError`) let the caller handle UI (dismiss dialog vs show error)
- `resetValidation()` clears errors when opening a fresh dialog

---

#### ValidationResult Sealed Class

**File:** `ValidationResult.kt`

```kotlin
package com.udacity.project.app

/**
 * Sealed class representing the result of form validation.
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}
```

**Key Points:**

- Two states: `Valid` (no error) and `Invalid` (with message)
- Sealed class ensures exhaustive `when` handling — compiler warns if a case is missed
- `Invalid` carries the error message to display in the UI

---

### Part 2: Display Validation in UI

#### TODO Step 3: Create Dialog Layout with TextInputLayout

**File:** `dialog_add_task.xml`

```xml

<com.google.android.material.textfield.TextInputLayout android:id="@+id/taskTitleInputLayout"
    android:layout_width="match_parent" android:layout_height="wrap_content" app:errorEnabled="true"
    app:counterEnabled="true" app:counterMaxLength="100">

    <com.google.android.material.textfield.TextInputEditText android:id="@+id/taskTitleEditText"
        android:layout_width="match_parent" android:layout_height="wrap_content"
        android:hint="Task title" android:inputType="textCapSentences" android:maxLength="100" />

</com.google.android.material.textfield.TextInputLayout>
```

**Key Points:**

- `TextInputLayout` wraps `TextInputEditText` to provide error display, character counter, and
  helper text
- `app:errorEnabled="true"` reserves space for error messages
- `app:counterEnabled="true"` shows character count (e.g., "15/100")
- `android:maxLength="100"` enforces hard limit at the input level

---

#### TODO Step 4: Connect Validation to UI

**File:** `MainActivity.kt`

```kotlin
private fun showAddTaskDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
    val inputLayout = dialogView.findViewById<TextInputLayout>(R.id.taskTitleInputLayout)
    val editText = dialogView.findViewById<TextInputEditText>(R.id.taskTitleEditText)

    viewModel.resetValidation()

    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle("Add New Task")
        .setView(dialogView)
        .setPositiveButton("Add", null)
        .setNegativeButton("Cancel", null)
        .create()

    dialog.setOnShowListener {
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false

        // Observe validation state
        viewModel.taskTitleValidation.observe(this) { validation ->
            when (validation) {
                is ValidationResult.Invalid -> {
                    inputLayout.error = validation.message
                    inputLayout.isErrorEnabled = true
                    positiveButton.isEnabled = false
                }
                ValidationResult.Valid -> {
                    inputLayout.error = null
                    inputLayout.isErrorEnabled = false
                    val title = editText.text?.toString() ?: ""
                    positiveButton.isEnabled = title.isNotBlank()

                    if (title.length >= 3) {
                        inputLayout.helperText = "Looks good!"
                    } else {
                        inputLayout.helperText = "Enter at least 3 characters"
                    }
                }
            }
        }

        // Real-time validation via TextWatcher
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val title = s?.toString() ?: ""
                viewModel.validateTaskTitleAsync(title)
            }
        })

        // Handle positive button click
        positiveButton.setOnClickListener {
            val title = editText.text?.toString() ?: ""
            viewModel.addTask(
                title = title,
                onSuccess = {
                    dialog.dismiss()
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Task added successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                },
                onError = {
                    // Error already shown via validation LiveData
                }
            )
        }
    }

    dialog.show()

    // Show keyboard automatically
    editText.requestFocus()
    editText.postDelayed({
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }, 100)
}
```

**Key Points:**

- `setPositiveButton("Add", null)` with null listener prevents auto-dismiss on click
- Button starts disabled — enabled only when validation passes
- TextWatcher triggers async validation on every keystroke
- `inputLayout.error = message` shows red error text below the field
- `inputLayout.error = null` clears the error
- `inputLayout.helperText` provides positive feedback when input is valid
- Keyboard shows automatically for better UX

---

## Validation Flow

```
User types character
        │
        ▼
TextWatcher.afterTextChanged()
        │
        ▼
viewModel.validateTaskTitleAsync()
        │
        ├─── Basic rules fail? → Update LiveData immediately
        │                         (empty, too short, too long)
        │
        └─── Basic rules pass? → Debounce 300ms
                                       │
                                       ▼
                                 Check duplicates
                                       │
                                       ▼
                                 Update LiveData
                                       │
                                       ▼
                              UI observes LiveData
                                       │
                           ┌───────────┴───────────┐
                           │                       │
                     Valid                    Invalid
                     • Clear error             • Show error
                     • Show helper text        • Disable button
                     • Enable button
```

## Testing the Implementation

### Test Case 1: Empty Title

1. Open add task dialog
2. Leave the title field empty
3. "Add" button remains disabled
4. Helper text shows "Enter at least 3 characters"

### Test Case 2: Title Too Short

1. Type "AB" (2 characters)
2. Error appears: "Task title must be at least 3 characters"
3. "Add" button disabled
4. Type one more character — error clears

### Test Case 3: Title Too Long

1. Type 101+ characters
2. Error appears: "Task title must not exceed 100 characters"
3. Character counter shows red (e.g., "101/100")

### Test Case 4: Duplicate Title

1. Type an existing task title (e.g., "Build task manager app")
2. After 300ms debounce, error appears: "A task with this title already exists"
3. Case-insensitive: "build TASK manager app" also triggers duplicate error

### Test Case 5: Valid Title

1. Type a valid unique title (3-100 characters)
2. Helper text shows "Looks good!"
3. "Add" button becomes enabled
4. Tap "Add" — dialog dismisses, task appears in list, Snackbar confirms

### Test Case 6: Debounce Behavior

1. Type quickly
2. Validation doesn't fire on every keystroke
3. Only the final input (after 300ms pause) is checked for duplicates

## Summary

This implementation demonstrates:

- ✅ Sealed class `ValidationResult` for type-safe validation states
- ✅ Synchronous validation rules (empty, length) with immediate feedback
- ✅ Asynchronous validation (duplicates) with debouncing
- ✅ TextInputLayout with error display and character counter
- ✅ TextWatcher for real-time validation on every keystroke
- ✅ LiveData observation for reactive UI updates
- ✅ Material Design dialog with validation-aware button state
- ✅ Helper text for positive feedback when input is valid

The form validation pattern is production-ready and provides clear, immediate feedback to guide
users toward valid input.
