package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

/**
 * MainActivity demonstrating coroutine usage with viewModelScope.
 * Shows sync operations, progress tracking, and cancellation.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel

    // Task UI
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    // Sync UI
    private lateinit var syncButton: Button
    private lateinit var syncWithProgressButton: Button
    private lateinit var cancelSyncButton: Button
    private lateinit var syncProgressBar: ProgressBar
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get ViewModel instance using ViewModelProvider
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeSyncState()
        observeSyncProgress()
        updateUI()
    }

    private fun initializeViews() {
        // Task views
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)

        // Sync views
        syncButton = findViewById(R.id.syncButton)
        syncWithProgressButton = findViewById(R.id.syncWithProgressButton)
        cancelSyncButton = findViewById(R.id.cancelSyncButton)
        syncProgressBar = findViewById(R.id.syncProgressBar)
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
        // Add task button
        addTaskButton.setOnClickListener {
            val title = taskInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTask(title)
                updateUI()
                taskInputEditText.text.clear()
            }
        }

        // Sync button - basic sync without progress
        syncButton.setOnClickListener {
            viewModel.syncTasks()
        }

        // Sync with progress button - shows progress updates
        syncWithProgressButton.setOnClickListener {
            viewModel.syncTasksWithProgress()
        }

        // Cancel sync button
        cancelSyncButton.setOnClickListener {
            viewModel.cancelSync()
        }
    }

    /**
     * TODO 2.1: Observes sync state using lifecycleScope to collect StateFlow.
     * Demonstrates how to observe coroutine-based state in the UI.
     */
    private fun observeSyncState() {
        lifecycleScope.launch {
            viewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Idle -> {
                        syncStatusTextView.visibility = View.GONE
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                    }
                    is SyncState.Loading -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Syncing tasks..."
                        enableSyncButtons(false)
                        cancelSyncButton.isEnabled = true
                    }
                    is SyncState.Success -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Sync completed successfully!"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                        updateUI()

                        // Hide success message after delay
                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 2000)
                    }
                    is SyncState.Error -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Error: ${state.message}"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                        Toast.makeText(this@MainActivity, "Sync failed: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    is SyncState.Cancelled -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Sync cancelled"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)

                        // Hide cancelled message after delay
                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 2000)
                    }
                }
            }
        }
    }

    /**
     * TODO 2.1: Observes sync progress updates.
     * Demonstrates progress tracking for long-running operations.
     */
    private fun observeSyncProgress() {
        lifecycleScope.launch {
            viewModel.syncProgress.collect { progress ->
                if (progress > 0) {
                    syncProgressBar.visibility = View.VISIBLE
                    syncProgressBar.progress = progress
                } else {
                    syncProgressBar.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Enables or disables sync buttons.
     */
    private fun enableSyncButtons(enabled: Boolean) {
        syncButton.isEnabled = enabled
        syncWithProgressButton.isEnabled = enabled
        cancelSyncButton.isEnabled = !enabled
    }

    /**
     * Updates the UI by getting data from the ViewModel.
     */
    private fun updateUI() {
        // Get tasks from ViewModel and update adapter
        taskAdapter.updateTasks(viewModel.getTasks())

        // Get counts and update TextViews
        totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"
        completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
    }
}