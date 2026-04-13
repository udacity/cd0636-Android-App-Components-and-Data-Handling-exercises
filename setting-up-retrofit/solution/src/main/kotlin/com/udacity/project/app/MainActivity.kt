package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

/**
 * MainActivity demonstrating Retrofit integration.
 * Shows how to make API calls and observe results in the UI.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var syncButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeSyncState()
        updateUI()
    }

    private fun initializeViews() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        syncButton = findViewById(R.id.syncButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
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
                viewModel.addTask(title)
                updateUI()
                taskInputEditText.text.clear()
            }
        }

        // Sync button triggers API call
        syncButton.setOnClickListener {
            viewModel.syncTasks()
        }
    }

    /**
     * Observes sync state from ViewModel and updates UI accordingly.
     * Demonstrates StateFlow collection with lifecycleScope.
     */
    private fun observeSyncState() {
        lifecycleScope.launch {
            viewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Idle -> {
                        syncStatusTextView.visibility = View.GONE
                        syncButton.isEnabled = true
                        syncButton.text = "Sync from API"
                    }
                    is SyncState.Loading -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Syncing from JSONPlaceholder API..."
                        syncButton.isEnabled = false
                        syncButton.text = "Syncing..."
                    }
                    is SyncState.Success -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "✓ Synced ${viewModel.getTotalTaskCount()} tasks from API"
                        syncButton.isEnabled = true
                        syncButton.text = "Sync from API"
                        updateUI()

                        // Hide success message after 3 seconds
                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 3000)

                        Toast.makeText(
                            this@MainActivity,
                            "Synced ${viewModel.getTotalTaskCount()} tasks",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is SyncState.Error -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "✗ Sync failed: ${state.message}"
                        syncButton.isEnabled = true
                        syncButton.text = "Retry Sync"

                        Toast.makeText(
                            this@MainActivity,
                            "Sync failed: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        taskAdapter.updateTasks(viewModel.getTasks())
        totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"
        completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
    }
}