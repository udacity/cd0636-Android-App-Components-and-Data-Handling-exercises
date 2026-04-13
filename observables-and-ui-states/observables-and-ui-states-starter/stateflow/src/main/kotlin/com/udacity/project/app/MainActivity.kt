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

        // TODO 2.2.2: Call observeViewModelWithFlow() to set up StateFlow collectors
        // Replace this comment with: observeViewModelWithFlow()
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
                // TODO 2.2.1: Remove this updateUI() call - StateFlow collectors will handle it
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
                // TODO 2.2.1: Remove this updateUI() call - StateFlow collectors will handle it
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
                // TODO 2.2.1: Remove this updateUI() call - StateFlow collectors will handle it
                taskInputEditText.text.clear()
            }
        }
    }

    // TODO 2.2.3: Create observeViewModelWithFlow() method for StateFlow
    // Implement this method to collect from three StateFlows:
    // private fun observeViewModelWithFlow() {
    //     lifecycleScope.launch {
    //         repeatOnLifecycle(Lifecycle.State.STARTED) {
    //             launch {
    //                 viewModel.tasksFlow.collect { tasks ->
    //                     taskAdapter.updateTasks(tasks)
    //                 }
    //             }
    //             launch {
    //                 viewModel.totalTaskCountFlow.collect { count ->
    //                     totalTasksTextView.text = "Total: $count"
    //                 }
    //             }
    //             launch {
    //                 viewModel.completedTaskCountFlow.collect { count ->
    //                     completedTasksTextView.text = "Completed: $count"
    //                 }
    //             }
    //         }
    //     }
    // }
}