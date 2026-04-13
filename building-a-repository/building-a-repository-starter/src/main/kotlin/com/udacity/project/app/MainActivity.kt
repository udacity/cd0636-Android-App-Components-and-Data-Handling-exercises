package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * MainActivity with task management.
 *
 * Complete Part 3 TODOs after implementing Repository and ViewModel (Parts 1 & 2).
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var syncButton: Button
    private lateinit var syncStatusTextView: TextView
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        // TODO 3.1: Trigger auto-sync on app start
        // Uncomment after completing Part 1 (TaskRepository) and Part 2 (TaskViewModel):
        // viewModel.syncIfNeeded()
        //
        // This triggers automatic sync ONLY if cache is stale (> 5 minutes old).
        // Cached data displays immediately, sync happens in background.
    }

    private fun initializeViews() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        syncButton = findViewById(R.id.syncButton)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskToggled = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
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
                taskInputEditText.text.clear()
            }
        }

        // Manual sync button - force refresh from network
        syncButton.setOnClickListener {
            viewModel.syncTasks()
        }
    }

    private fun observeViewModel() {
        // Observe task list
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }

        // Observe task count statistics
        viewModel.totalTaskCount.observe(this) { count ->
            totalTasksTextView.text = "Total: $count"
        }

        viewModel.completedTaskCount.observe(this) { count ->
            completedTasksTextView.text = "Completed: $count"
        }

        // TODO 3.2: Observe sync state for UI feedback
        // Uncomment after completing TODO 2.3 in TaskViewModel:
        //
        // viewModel.syncState.observe(this) { state ->
        //     when (state) {
        //         is SyncState.Idle -> {
        //             syncButton.isEnabled = true
        //             syncStatusTextView.text = ""
        //         }
        //         is SyncState.Loading -> {
        //             syncButton.isEnabled = false
        //             syncStatusTextView.text = "Syncing..."
        //         }
        //         is SyncState.Success -> {
        //             syncButton.isEnabled = true
        //             syncStatusTextView.text = "Synced ✓"
        //             // Clear success message after 2 seconds
        //             syncStatusTextView.postDelayed({
        //                 if (viewModel.syncState.value is SyncState.Success) {
        //                     syncStatusTextView.text = ""
        //                 }
        //             }, 2000)
        //         }
        //         is SyncState.Error -> {
        //             syncButton.isEnabled = true
        //             syncStatusTextView.text = "Sync failed: ${state.message}"
        //         }
        //     }
        // }
    }
}