package com.udacity.project.app

// ============================================================================
// PART 1: TaskViewModel - Business Logic and Data Management
// ============================================================================

// TODO: Step 1.1 - Make this class extend ViewModel
// Import androidx.lifecycle.ViewModel and extend it
// Example: class TaskViewModel : ViewModel() { ... }
class TaskViewModel {
    private val _tasks = mutableListOf<Task>()
    private var nextTaskId = 1

     init {
         _tasks.add(Task(id = nextTaskId++, title = "Sample Task 1", completed = false))
         _tasks.add(Task(id = nextTaskId++, title = "Sample Task 2", completed = true))
     }

    // Method to get the current list of tasks
    fun getTasks(): List<Task> {
        return _tasks.toList()
    }

    // TODO: Step 1.2 - Complete the addTask method
    fun addTask(title: String) {
        // Create a new task with the next available ID
        val newTask = Task(
            id = nextTaskId++,
            title = title,
            completed = false
        )

        // TODO: Add the new task to the _tasks list
        // Hint: Use _tasks.add(newTask)
    }

    // TODO: Step 1.3 - Complete the toggleTaskCompletion method
    fun toggleTaskCompletion(taskId: Int) {
        // TODO: Find the task by ID and toggle its completion status
        // Hint: Find the index using _tasks.indexOfFirst { it.id == taskId }
        // Then update the task at that index with task.copy(completed = !task.completed)
    }

    // TODO: Step 1.4 - Complete the deleteTask method
    fun deleteTask(taskId: Int) {
        // TODO: Remove the task with the specified ID from _tasks
        // Hint: Use _tasks.removeIf { it.id == taskId }
    }

    // Method to get the total number of tasks
    fun getTotalTaskCount(): Int {
        return _tasks.size
    }

    // Method to get the number of completed tasks
    fun getCompletedTaskCount(): Int {
        return _tasks.count { it.completed }
    }
}