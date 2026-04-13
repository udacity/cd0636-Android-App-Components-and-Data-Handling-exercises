package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.view.View

/**
 * MainActivity with coroutine support for observing async operations.
 * Complete the TODO steps to observe sync state and progress.
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

    // Sync UI (Already declared - no TODO needed)
    private lateinit var syncButton: Button
    private lateinit var syncWithProgressButton: Button
    private lateinit var cancelSyncButton: Button
    private lateinit var syncProgressBar: ProgressBar
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        // TODO 2.1: Call observeSyncState() here
        observeSyncProgress() // Pre-implemented as an example
        updateUI()
    }

    private fun initializeViews() {
        // Task views
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)

        // Sync views (Already initialized - no TODO needed)
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

        // Sync button click listeners (Already implemented - no TODO needed)
        syncButton.setOnClickListener {
            viewModel.syncTasks()
        }

        syncWithProgressButton.setOnClickListener {
            viewModel.syncTasksWithProgress()
        }

        cancelSyncButton.setOnClickListener {
            viewModel.cancelSync()
        }
    }

    /**
     * TODO 2.1: Observe sync state using lifecycleScope
     *
     * Complete this function to observe syncState from ViewModel:
     * 1. Use lifecycleScope.launch to start a coroutine
     * 2. Collect from viewModel.syncState using .collect { }
     * 3. Update UI based on state using when expression:
     *    - SyncState.Idle: Hide status and progress views
     *    - SyncState.Loading: Show "Syncing..." text, disable sync buttons, enable cancel
     *    - SyncState.Success: Show success message, call updateUI(), hide after 2s
     *    - SyncState.Error: Show error message with state.message
     *    - SyncState.Cancelled: Show "Sync cancelled" message, hide after 2s
     *
     * Hint: lifecycleScope.launch { viewModel.syncState.collect { state -> ... } }
     * Hint: Use enableSyncButtons(true/false) helper function
     * Hint: Use View.VISIBLE and View.GONE for visibility
     */
    private fun observeSyncState() {
        // TODO 2.1: Implement StateFlow collection with lifecycleScope
    }

    /**
     * Observes sync progress updates (Pre-implemented as an example).
     * Use this as a reference for implementing observeSyncState().
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
     * Helper function to enable/disable sync buttons
     */
    private fun enableSyncButtons(enabled: Boolean) {
        syncButton.isEnabled = enabled
        syncWithProgressButton.isEnabled = enabled
        cancelSyncButton.isEnabled = !enabled
    }

    private fun updateUI() {
        taskAdapter.updateTasks(viewModel.getTasks())
        totalTasksTextView.text = "Total: ${viewModel.getTotalTaskCount()}"
        completedTasksTextView.text = "Completed: ${viewModel.getCompletedTaskCount()}"
    }
}