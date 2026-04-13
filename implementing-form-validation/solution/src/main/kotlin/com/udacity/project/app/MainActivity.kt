package com.udacity.project.app

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * MainActivity with form validation for adding tasks.
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTaskButton: Button
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

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
        addTaskButton = findViewById(R.id.addTaskButton)
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
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }
    }

    /**
     * Shows dialog with validation for adding a new task.
     */
    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val inputLayout = dialogView.findViewById<TextInputLayout>(R.id.taskTitleInputLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.taskTitleEditText)

        // Reset validation state when opening dialog
        viewModel.resetValidation()

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add", null) // Set null to customize later
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.isEnabled = false

            // Observe validation state
            viewModel.taskTitleValidation.observe(this) { validation ->
                when (validation) {
                    is ValidationResult.Invalid -> {
                        inputLayout.error = validation.message
                        inputLayout.isErrorEnabled = true
                        positiveButton.isEnabled = false
                    }
                    ValidationResult.Valid -> {
                        inputLayout.error = null
                        inputLayout.isErrorEnabled = false
                        val title = editText.text?.toString() ?: ""
                        positiveButton.isEnabled = title.isNotBlank()

                        if (title.length >= 3) {
                            inputLayout.helperText = "Looks good!"
                        } else {
                            inputLayout.helperText = "Enter at least 3 characters"
                        }
                    }
                }
            }

            // Add text watcher for real-time validation
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val title = s?.toString() ?: ""
                    viewModel.validateTaskTitleAsync(title)
                }
            })

            // Handle positive button click
            positiveButton.setOnClickListener {
                val title = editText.text?.toString() ?: ""
                viewModel.addTask(
                    title = title,
                    onSuccess = {
                        dialog.dismiss()
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Task added successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        // Error already shown via validation LiveData
                    }
                )
            }
        }

        dialog.show()

        // Request focus and show keyboard
        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
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
    }
}