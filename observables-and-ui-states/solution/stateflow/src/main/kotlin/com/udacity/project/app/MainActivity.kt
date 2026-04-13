package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
        observeViewModelWithFlow()
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
    }

    private fun observeViewModelWithFlow() {
        // Launch a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // Repeat collection when lifecycle is at least STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Launch separate coroutines for each flow collection
                // Each collect() suspends indefinitely, so we need separate launch blocks

                // Collect tasks flow and update RecyclerView adapter
                launch {
                    viewModel.tasksFlow.collect { tasks ->
                        taskAdapter.updateTasks(tasks)
                    }
                }

                // Collect total count flow and update TextView
                launch {
                    viewModel.totalTaskCountFlow.collect { count ->
                        totalTasksTextView.text = "Total: $count"
                    }
                }

                // Collect completed count flow and update TextView
                launch {
                    viewModel.completedTaskCountFlow.collect { count ->
                        completedTasksTextView.text = "Completed: $count"
                    }
                }
            }
        }
    }
}