package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todosRecyclerView: RecyclerView
    private lateinit var todoInputEditText: EditText
    private lateinit var addTodoButton: Button
    private lateinit var syncButton: Button
    private lateinit var totalTodosTextView: TextView
    private lateinit var completedTodosTextView: TextView
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeSyncState()
        observeTodos()
    }

    private fun initializeViews() {
        todosRecyclerView = findViewById(R.id.todosRecyclerView)
        todoInputEditText = findViewById(R.id.todoInputEditText)
        addTodoButton = findViewById(R.id.addTodoButton)
        syncButton = findViewById(R.id.syncButton)
        totalTodosTextView = findViewById(R.id.totalTodosTextView)
        completedTodosTextView = findViewById(R.id.completedTodosTextView)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
    }

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter(
            onTodoToggled = { todoId ->
                viewModel.toggleCompleted(todoId)
            },
            onTodoDeleted = { todoId ->
                viewModel.removeTodo(todoId)
            }
        )
        todosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }
    }

    private fun setupClickListeners() {
        addTodoButton.setOnClickListener {
            val title = todoInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTodo(title)
                todoInputEditText.text.clear()
            }
        }

        syncButton.setOnClickListener {
            viewModel.syncTodos()
        }
    }

    /**
     * Observes sync state from ViewModel and updates UI accordingly.
     * Demonstrates StateFlow collection with lifecycleScope.
     */
    private fun observeSyncState() {
        lifecycleScope.launch {
            viewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Idle -> {
                        syncStatusTextView.visibility = View.GONE
                        syncButton.isEnabled = true
                        syncButton.text = "Sync from API"
                    }
                    is SyncState.Loading -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Syncing from JSONPlaceholder API..."
                        syncButton.isEnabled = false
                        syncButton.text = "Syncing..."
                    }
                    is SyncState.Success -> {
                        val count = viewModel.todos.value.size
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Synced $count todos from API"
                        syncButton.isEnabled = true
                        syncButton.text = "Sync from API"

                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 3000)

                        Toast.makeText(
                            this@MainActivity,
                            "Synced $count todos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is SyncState.Error -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Sync failed: ${state.message}"
                        syncButton.isEnabled = true
                        syncButton.text = "Retry Sync"

                        Toast.makeText(
                            this@MainActivity,
                            "Sync failed: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeTodos() {
        lifecycleScope.launch {
            viewModel.todos.collect { todos ->
                todoAdapter.updateTodos(todos)
                totalTodosTextView.text = "Total: ${todos.size}"
                completedTodosTextView.text = "Completed: ${todos.count { it.completed }}"
            }
        }
    }
}