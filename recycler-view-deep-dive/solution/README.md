# Task Manager with ListAdapter and DiffUtil - Solution

## Overview

This solution demonstrates upgrading a basic RecyclerView.Adapter to a ListAdapter with DiffUtil for
efficient list updates and automatic animations. It covers creating DiffUtil.ItemCallback for item
comparison, using submitList() for data updates, implementing payload-based partial updates, and
adding swipe-to-delete with undo functionality.

## Implementation Guide

This guide provides detailed step-by-step instructions with complete code for each TODO.

### Part 1: Implement ListAdapter with DiffUtil

#### TODO 1.1: Create TaskDiffCallback

**File:** `TaskAdapter.kt`

Create a DiffUtil.ItemCallback to calculate the difference between two lists:

```kotlin
/**
 * DiffUtil.ItemCallback to calculate the difference between two lists.
 */
class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        // Check if items represent the same task (by ID)
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        // Check if all fields are the same
        return oldItem == newItem
    }

    /**
     * Return payload to specify what changed for partial updates.
     */
    override fun getChangePayload(oldItem: Task, newItem: Task): Any? {
        return when {
            oldItem.completed != newItem.completed -> PAYLOAD_COMPLETED
            oldItem.isFavorite != newItem.isFavorite -> PAYLOAD_FAVORITE
            else -> null
        }
    }
}
```

**Key Points:**

- `areItemsTheSame()` checks identity — do these represent the same item? Compare by unique ID
- `areContentsTheSame()` checks equality — has anything changed? Uses data class `==`
- `getChangePayload()` identifies what specifically changed for partial rebinding
- DiffUtil runs the comparison on a background thread automatically

---

#### TODO 1.2: Convert to ListAdapter

**File:** `TaskAdapter.kt`

Change the class to extend `ListAdapter` instead of `RecyclerView.Adapter`:

```kotlin
package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * ListAdapter with DiffUtil for efficient list updates and animations.
 */
class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onToggleComplete: (Task) -> Unit,
    private val onToggleFavorite: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    // No tasks property needed — ListAdapter manages the list internally
    // No getItemCount() override needed — ListAdapter handles it
    // No updateTasks() method needed — use submitList() instead

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Bind with payloads for partial updates.
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val task = getItem(position)
            payloads.forEach { payload ->
                when (payload) {
                    PAYLOAD_COMPLETED -> holder.updateCompletion(task.completed)
                    PAYLOAD_FAVORITE -> holder.updateFavorite(task.isFavorite)
                }
            }
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.completed
            updateFavoriteIcon(task.isFavorite)

            itemView.setOnClickListener { onTaskClick(task) }
            taskCheckbox.setOnClickListener { onToggleComplete(task) }
            favoriteButton.setOnClickListener { onToggleFavorite(task) }
        }

        fun updateCompletion(completed: Boolean) {
            taskCheckbox.isChecked = completed
        }

        fun updateFavorite(favorite: Boolean) {
            updateFavoriteIcon(favorite)
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.star_big_on
                else android.R.drawable.star_big_off
            )
        }
    }

    companion object {
        private const val PAYLOAD_COMPLETED = "payload_completed"
        private const val PAYLOAD_FAVORITE = "payload_favorite"
    }
}
```

**Key Points:**

- `ListAdapter<Task, TaskViewHolder>(TaskDiffCallback())` replaces `RecyclerView.Adapter`
- `getItem(position)` replaces manual `tasks[position]` access
- Remove: `tasks` property, `updateTasks()`, `getItemCount()` — all handled by ListAdapter
- Use `submitList()` from the caller instead of `updateTasks()`
- Payload-based binding updates only the changed view (checkbox or star), not the entire row

**What ListAdapter Removes:**

| Before (RecyclerView.Adapter)   | After (ListAdapter)         |
|---------------------------------|-----------------------------|
| `private var tasks: List<Task>` | Managed internally          |
| `fun updateTasks(newTasks)`     | `submitList(newList)`       |
| `override fun getItemCount()`   | Handled automatically       |
| `notifyDataSetChanged()`        | DiffUtil calculates changes |

---

### Part 2: Add ViewModel Methods

#### TODO 2.1: Implement toggleTaskFavorite()

**File:** `TaskViewModel.kt`

```kotlin
fun toggleTaskFavorite(taskId: Int, isFavorite: Boolean) {
    val currentTasks = tasks.value ?: emptyList()
    val updatedTasks = currentTasks.map { task ->
        if (task.id == taskId) {
            task.copy(isFavorite = isFavorite)
        } else {
            task
        }
    }
    savedStateHandle["tasks"] = updatedTasks
}
```

---

#### TODO 2.2: Implement addTaskWithDetails()

```kotlin
fun addTaskWithDetails(task: Task) {
    val currentTasks = tasks.value ?: emptyList()
    val updatedTasks = currentTasks + task
    savedStateHandle["tasks"] = updatedTasks
}
```

**Key Points:**

- `addTaskWithDetails()` restores a complete task object (useful for undo after delete)
- Both methods create new list instances — important because `submitList()` skips processing if it
  receives the same reference

---

### Part 3: Update MainActivity

#### TODO 3.1: Update to Use submitList()

**File:** `MainActivity.kt`

Replace `updateTasks()` with `submitList()` in the observer:

```kotlin
private fun observeViewModel() {
    viewModel.tasks.observe(this) { tasks ->
        // Use submitList() instead of updateTasks()
        taskAdapter.submitList(tasks)
    }

    viewModel.totalTaskCount.observe(this) { count ->
        totalTasksTextView.text = "Total: $count"
    }

    viewModel.completedTaskCount.observe(this) { count ->
        completedTasksTextView.text = "Completed: $count"
    }
}
```

**Add swipe-to-delete with undo:**

```kotlin
private fun setupSwipeToDelete() {
    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val task = taskAdapter.currentList[position]

            viewModel.deleteTask(task.id)

            Snackbar.make(
                findViewById(android.R.id.content),
                "Task deleted",
                Snackbar.LENGTH_LONG
            ).setAction("Undo") {
                viewModel.addTaskWithDetails(task)
            }.show()
        }
    })

    itemTouchHelper.attachToRecyclerView(tasksRecyclerView)
}
```

**Key Points:**

- `submitList()` triggers DiffUtil to calculate minimal changes on a background thread
- `currentList` provides access to the adapter's current data for swipe handling
- Snackbar with "Undo" action uses `addTaskWithDetails()` to restore deleted tasks
- `ItemTouchHelper` handles swipe gestures with red background feedback

---

## DiffUtil vs notifyDataSetChanged()

```
notifyDataSetChanged()          DiffUtil + submitList()
────────────────────────        ──────────────────────────
Invalidates ALL items           Calculates minimal changes
No animations                   Automatic add/remove/move animations
Rebinds every ViewHolder        Only rebinds changed items
O(n) rebinding work             O(n) diff + O(changed) rebinding
Flashes entire list             Smooth, targeted updates
Main thread                     Diff runs on background thread
```

## Testing the Implementation

### Test Case 1: Automatic Animations

1. Add a new task
2. Observe smooth insertion animation
3. Delete a task (swipe)
4. Observe smooth removal animation

### Test Case 2: Partial Updates with Payloads

1. Toggle a task's completion checkbox
2. Only the checkbox updates — no full row rebind
3. Toggle a task's favorite star
4. Only the star icon updates

### Test Case 3: Swipe-to-Delete with Undo

1. Swipe a task left or right
2. Red background appears during swipe
3. Task is removed with animation
4. Snackbar appears with "Undo" action
5. Tap "Undo" — task is restored

### Test Case 4: Performance

1. Scroll through a long list quickly
2. No visible lag or stuttering
3. Add/remove items while scrolling
4. Animations remain smooth

### Test Case 5: Same List Reference

1. Submit the same list reference twice
2. DiffUtil correctly skips processing (no-op)
3. No unnecessary rebinding occurs

## Common Issues and Solutions

### Issue 1: submitList() Does Nothing

**Problem:** Submitting the same list reference doesn't trigger updates.

**Solution:** Always submit a new list instance:

```kotlin
// Wrong — same reference
submitList(myList)

// Correct — new instance
submitList(myList.toList())
```

---

### Issue 2: Animations Not Playing

**Problem:** Items appear/disappear without animation.

**Solution:** Ensure `key` (the `id` field) is stable and unique. `areItemsTheSame()` must compare
by a consistent identifier.

---

### Issue 3: Payloads Not Working

**Problem:** Full rebind happens even when only one field changed.

**Solution:** Override `onBindViewHolder(holder, position, payloads)` and handle non-empty payloads
before calling `super`.

---

## Summary

This implementation demonstrates:

- ✅ ListAdapter with DiffUtil for efficient list updates
- ✅ DiffUtil.ItemCallback with areItemsTheSame and areContentsTheSame
- ✅ Payload-based partial updates for checkbox and favorite changes
- ✅ submitList() replacing manual notifyDataSetChanged()
- ✅ Background thread diffing for smooth UI performance
- ✅ Swipe-to-delete with undo functionality
- ✅ Automatic item animations (add, remove, change)

The adapter is now production-ready with minimal UI updates and smooth animations for all list
changes.
