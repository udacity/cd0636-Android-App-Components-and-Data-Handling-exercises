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

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        // TODO 1.2.2: Call observeViewModel() to set up LiveData observers
        // Replace this comment with: observeViewModel()
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
                // TODO 1.2.1: Remove this updateUI() call - LiveData observers will handle it
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
                // TODO 1.2.1: Remove this updateUI() call - LiveData observers will handle it
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
                // TODO 1.2.1: Remove this updateUI() call - LiveData observers will handle it
                taskInputEditText.text.clear()
            }
        }
    }

    // TODO 1.2.3: Create observeViewModel() method for LiveData
    // Implement this method to set up three observers:
    // private fun observeViewModel() {
    //     viewModel.tasks.observe(this) { tasks ->
    //         taskAdapter.updateTasks(tasks)
    //     }
    //     viewModel.totalTaskCount.observe(this) { count ->
    //         totalTasksTextView.text = "Total: $count"
    //     }
    //     viewModel.completedTaskCount.observe(this) { count ->
    //         completedTasksTextView.text = "Completed: $count"
    //     }
    // }
}