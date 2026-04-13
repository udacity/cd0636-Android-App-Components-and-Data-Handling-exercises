package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Step 1: Get ViewModel instance using ViewModelProvider
        // ViewModelProvider ensures the same ViewModel instance is returned across configuration changes
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

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
            // Step 2: Delegate user actions to ViewModel methods
            // Instead of managing data in the Activity, delegate to the ViewModel
            // After calling the ViewModel method, call updateUI() to refresh the display
            onTaskToggled = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
                updateUI()
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
                updateUI()
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
                // Step 3: Call ViewModel's addTask method
                // The ViewModel handles the business logic
                viewModel.addTask(title)
                // Step 4: Update the UI after data changes
                updateUI()
                taskInputEditText.text.clear()
            }
        }
    }

    // Step 4: Update UI by getting data from the ViewModel
    // This method is called whenever the data changes to refresh the display
    private fun updateUI() {
        // Get tasks from ViewModel and update adapter
        taskAdapter.updateTasks(viewModel.getTasks())

        // Get counts and update TextViews
        totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"
        completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
    }
}