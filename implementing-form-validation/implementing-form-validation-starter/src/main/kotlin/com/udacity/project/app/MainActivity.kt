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
 * MainActivity with basic task management.
 * TODO: Add form validation with dialog.
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskInputEditText: EditText
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
        taskInputEditText = findViewById(R.id.taskInputEditText)
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
            val title = taskInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTask(title)
                taskInputEditText.text.clear()
            }
        }

        // TODO: Step 4 - Replace inline input with dialog
        // addTaskButton.setOnClickListener {
        //     showAddTaskDialog()
        // }
    }

    // TODO: Step 4 - Create showAddTaskDialog method
    // private fun showAddTaskDialog() {
    //     // Inflate dialog layout
    //     val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
    //     val inputLayout = dialogView.findViewById<TextInputLayout>(R.id.taskTitleInputLayout)
    //     val editText = dialogView.findViewById<TextInputEditText>(R.id.taskTitleEditText)
    //
    //     // Reset validation state
    //     viewModel.resetValidation()
    //
    //     // Create MaterialAlertDialog
    //     val dialog = MaterialAlertDialogBuilder(this)
    //         .setTitle("Add New Task")
    //         .setView(dialogView)
    //         .setPositiveButton("Add", null)
    //         .setNegativeButton("Cancel", null)
    //         .create()
    //
    //     dialog.setOnShowListener {
    //         val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
    //         positiveButton.isEnabled = false
    //
    //         // Observe validation state
    //         viewModel.taskTitleValidation.observe(this) { validation ->
    //             when (validation) {
    //                 is ValidationResult.Invalid -> {
    //                     inputLayout.error = validation.message
    //                     inputLayout.isErrorEnabled = true
    //                     positiveButton.isEnabled = false
    //                 }
    //                 ValidationResult.Valid -> {
    //                     inputLayout.error = null
    //                     inputLayout.isErrorEnabled = false
    //                     val title = editText.text?.toString() ?: ""
    //                     positiveButton.isEnabled = title.isNotBlank()
    //
    //                     if (title.length >= 3) {
    //                         inputLayout.helperText = "Looks good!"
    //                     } else {
    //                         inputLayout.helperText = "Enter at least 3 characters"
    //                     }
    //                 }
    //             }
    //         }
    //
    //         // Add TextWatcher for real-time validation
    //         editText.addTextChangedListener(object : TextWatcher {
    //             override fun afterTextChanged(s: Editable?) {
    //                 val title = s?.toString() ?: ""
    //                 viewModel.validateTaskTitleAsync(title)
    //             }
    //             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    //             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    //         })
    //
    //         // Handle positive button click
    //         positiveButton.setOnClickListener {
    //             val title = editText.text?.toString() ?: ""
    //             viewModel.addTask(
    //                 title = title,
    //                 onSuccess = {
    //                     dialog.dismiss()
    //                     Snackbar.make(
    //                         findViewById(android.R.id.content),
    //                         "Task added successfully",
    //                         Snackbar.LENGTH_SHORT
    //                     ).show()
    //                 },
    //                 onError = {
    //                     // Error already shown via validation LiveData
    //                 }
    //             )
    //         }
    //     }
    //
    //     dialog.show()
    //
    //     // Show keyboard
    //     editText.requestFocus()
    //     editText.postDelayed({
    //         val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //         imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    //     }, 100)
    // }

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
