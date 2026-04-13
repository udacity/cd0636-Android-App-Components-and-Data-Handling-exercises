package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

/**
 * MainActivity with error handling and sync functionality.
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var syncButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    // Error state views
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var errorSubtitle: TextView
    private lateinit var retryButton: Button
    private lateinit var loadingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        taskInputEditText = findViewById(R.id.taskInputEditText)
        addTaskButton = findViewById(R.id.addTaskButton)
        syncButton = findViewById(R.id.syncButton)
        totalTasksTextView = findViewById(R.id.totalTasksTextView)
        completedTasksTextView = findViewById(R.id.completedTasksTextView)

        // Error state views
        errorContainer = findViewById(R.id.errorContainer)
        errorIcon = findViewById(R.id.errorIcon)
        errorMessage = findViewById(R.id.errorMessage)
        errorSubtitle = findViewById(R.id.errorSubtitle)
        retryButton = findViewById(R.id.retryButton)
        loadingProgress = findViewById(R.id.loadingProgress)

        // Initially hide error and loading states
        errorContainer.visibility = View.GONE
        loadingProgress.visibility = View.GONE
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
        addTaskButton.setOnClickListener {
            val title = taskInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTask(title)
                taskInputEditText.text.clear()
            }
        }

        syncButton.setOnClickListener {
            viewModel.syncTasks()
        }

        retryButton.setOnClickListener {
            viewModel.syncTasks()
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }

        viewModel.totalTaskCount.observe(this) { count ->
            totalTasksTextView.text = "Total: $count"
        }

        viewModel.completedTaskCount.observe(this) { count ->
            completedTasksTextView.text = "Completed: $count"
        }

        // TODO: Step 2.1 - Observe sync state
        // Observe viewModel.syncState and handle different states:
        // - NetworkResult.Loading: Show loading progress, hide error container, disable sync button
        // - NetworkResult.Success: Hide loading, hide error, enable sync button, show success Snackbar
        // - NetworkResult.Error: Hide loading, enable sync button, call showErrorState(state.error)
        // - null: Reset to initial state (hide loading, hide error, enable sync button)
    }

    // TODO: Step 2.2 - Implement showErrorState(error: NetworkError) function
    // This function should display errors:
    //
    // 1. Show error container: errorContainer.visibility = View.VISIBLE
    //    Hide RecyclerView: tasksRecyclerView.visibility = View.GONE
    //
    // 2. Set appropriate icon based on error type:
    //    - NoInternet: android.R.drawable.ic_dialog_alert
    //    - Timeout: android.R.drawable.ic_dialog_info
    //    - ServerError: android.R.drawable.ic_dialog_alert
    //    - NotFound: android.R.drawable.ic_menu_search
    //    - Unknown: android.R.drawable.ic_dialog_alert
    //
    // 3. Apply red color filter to icon:
    //    errorIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))
    //
    // 4. Set error message: errorMessage.text = error.message
    //
    // 5. Set actionable subtitle based on error type:
    //    - NoInternet: "Check your internet connection and try again"
    //    - Timeout: "The request took too long. Try again later"
    //    - ServerError: "Our servers are having issues. Try again later"
    //    - NotFound: "The requested data could not be found"
    //    - Unknown: "Something went wrong. Please try again"
    //
    // 6. Enable retry button: retryButton.isEnabled = true
}
