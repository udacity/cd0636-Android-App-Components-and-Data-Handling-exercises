package com.udacity.project.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * MainActivity demonstrating LazyColumn as a RecyclerView alternative.
 * Compare with TaskAdapter.kt to see the differences.
 */
class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TaskListScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Main screen composable with Scaffold structure.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        TaskList(
            tasks = tasks,
            onToggleComplete = { task ->
                viewModel.toggleCompletion(task.id, !task.completed)
            },
            onToggleFavorite = { task ->
                viewModel.toggleFavorite(task.id, !task.isFavorite)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAddTask = { title ->
                viewModel.addTask(title)
                showAddDialog = false
            }
        )
    }
}

/**
 * TODO: Implement TaskList with LazyColumn
 *
 * LazyColumn is Compose's replacement for RecyclerView.
 * Compare with TaskAdapter.kt to see the differences.
 *
 * Steps:
 * 1. Check if tasks list is empty, show EmptyState if true
 * 2. Create LazyColumn with fillMaxSize() and contentPadding
 * 3. Use items() function with tasks list and key parameter
 * 4. For each task, render TaskItem and HorizontalDivider
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    onToggleComplete: (Task) -> Unit,
    onToggleFavorite: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Step 1 - Check if tasks is empty
    // if (tasks.isEmpty()) {
    //     EmptyState(modifier = modifier)
    // } else {
    //     // Show LazyColumn
    // }

    // TODO: Step 2 - Create LazyColumn
    // LazyColumn(
    //     modifier = modifier.fillMaxSize(),
    //     contentPadding = PaddingValues(vertical = 8.dp)
    // ) {
    //     // Add items here
    // }

    // TODO: Step 3 - Use items() function inside LazyColumn
    // items(
    //     items = tasks,
    //     key = { task -> task.id }  // Important: unique key for each item
    // ) { task ->
    //     // Render each task here
    // }

    // TODO: Step 4 - Inside items block, render TaskItem
    // TaskItem(
    //     task = task,
    //     onToggleComplete = { onToggleComplete(task) },
    //     onToggleFavorite = { onToggleFavorite(task) }
    // )
    // HorizontalDivider()
}

/**
 * TaskItem composable for individual task display.
 * Compare with TaskAdapter.TaskViewHolder to see how this replaces ViewHolder pattern.
 */
@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { /* Could navigate to detail screen */ })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { onToggleComplete() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = task.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (task.completed) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            },
            color = if (task.completed) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (task.isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = if (task.isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                },
                tint = if (task.isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Empty state composable shown when no tasks exist.
 */
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to add your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Dialog composable for adding new tasks.
 */
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            TextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Task title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onAddTask(taskTitle) },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}