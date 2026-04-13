package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// ============================================================================
// PART 2: MainActivity - UI Layer and ViewModel Integration
// ============================================================================

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Step 2.1 - Get an instance of TaskViewModel using ViewModelProvider
        // You need to obtain a ViewModel instance using ViewModelProvider. ViewModelProvider
        // is a utility class that provides ViewModels for a given lifecycle scope, ensuring
        // the same instance is returned across configuration changes.
        // Complete the following line:
        // viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        updateUI()
    }

    private fun initializeViews() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            // TODO: Step 2.2 - Call ViewModel methods in adapter callbacks
            // The adapter callbacks need to call the corresponding ViewModel methods to handle
            // user actions. Instead of managing data in the Activity, delegate to the ViewModel.
            // After calling the ViewModel method, call updateUI() to refresh the UI.
            onTaskToggled = { taskId ->
                // TODO: Call viewModel.toggleTaskCompletion(taskId)
                // TODO: Call updateUI()
            },
            onTaskDeleted = { taskId ->
                // TODO: Call viewModel.deleteTask(taskId)
                // TODO: Call updateUI()
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
                // TODO: Step 2.3 - Call ViewModel addTask method
                // When the user clicks the add button, call the ViewModel's addTask method
                // to add the new task. After adding, call updateUI() to refresh the UI.
                // Example:
                // viewModel.addTask(title)
                // updateUI()
                taskInputEditText.text.clear()
            }
        }
    }

    // TODO: Step 2.4 - Complete the updateUI method
    // This method should refresh the UI by getting data from the ViewModel and updating the views.
    // Call this method after any operation that changes the task list.
    private fun updateUI() {
        // TODO: Get the list of tasks from the ViewModel and update the adapter
        // Example: taskAdapter.updateTasks(viewModel.getTasks())

        // TODO: Get the total task count and update the TextView
        // Example: totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"

        // TODO: Get the completed task count and update the TextView
        // Example: completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
    }
}