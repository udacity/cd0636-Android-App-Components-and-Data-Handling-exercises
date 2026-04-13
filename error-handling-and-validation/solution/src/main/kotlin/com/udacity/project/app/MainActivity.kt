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

        // Observe sync state
        viewModel.syncState.observe(this) { state ->
            when (state) {
                is NetworkResult.Loading -> {
                    loadingProgress.visibility = View.VISIBLE
                    errorContainer.visibility = View.GONE
                    syncButton.isEnabled = false
                    tasksRecyclerView.visibility = View.VISIBLE
                }
                is NetworkResult.Success -> {
                    loadingProgress.visibility = View.GONE
                    errorContainer.visibility = View.GONE
                    syncButton.isEnabled = true
                    tasksRecyclerView.visibility = View.VISIBLE

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Tasks synced successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Error -> {
                    loadingProgress.visibility = View.GONE
                    syncButton.isEnabled = viewModel.canRetrySync()

                    showErrorState(state.error)
                }
                null -> {
                    // Initial state or reset
                    loadingProgress.visibility = View.GONE
                    errorContainer.visibility = View.GONE
                    syncButton.isEnabled = true
                    tasksRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showErrorState(error: NetworkError) {
        // Determine if we should show full error state or just a Snackbar
        val shouldShowFullError = error is NetworkError.NoInternet ||
                                  error is NetworkError.ServerError

        if (shouldShowFullError) {
            errorContainer.visibility = View.VISIBLE
            tasksRecyclerView.visibility = View.GONE

            // Set error icon based on error type
            val iconRes = when (error) {
                is NetworkError.NoInternet -> android.R.drawable.ic_dialog_alert
                is NetworkError.Timeout -> android.R.drawable.ic_dialog_info
                is NetworkError.ServerError -> android.R.drawable.ic_dialog_alert
                is NetworkError.NotFound -> android.R.drawable.ic_menu_search
                is NetworkError.Unknown -> android.R.drawable.ic_dialog_alert
            }
            errorIcon.setImageResource(iconRes)
            errorIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))

            // Set error message
            errorMessage.text = error.message

            // Set error subtitle with actionable advice
            val subtitle = when (error) {
                is NetworkError.NoInternet -> "Check your internet connection and try again"
                is NetworkError.Timeout -> "The request took too long. Try again later"
                is NetworkError.ServerError -> "Our servers are having issues. Try again later"
                is NetworkError.NotFound -> "The requested data could not be found"
                is NetworkError.Unknown -> "Something went wrong. Please try again"
            }
            errorSubtitle.text = subtitle

            // Setup retry button
            retryButton.isEnabled = viewModel.canRetrySync()
        } else {
            // Show as Snackbar for less critical errors
            errorContainer.visibility = View.GONE
            tasksRecyclerView.visibility = View.VISIBLE

            Snackbar.make(findViewById(android.R.id.content), error.message, Snackbar.LENGTH_LONG)
                .setAction("Retry") {
                    if (viewModel.canRetrySync()) {
                        viewModel.syncTasks()
                    }
                }
                .show()
        }
    }
}