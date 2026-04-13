# Exercise 26 Solution: Task Manager with Jetpack Compose Lists

This document provides the complete solution implementation for Exercise 26, demonstrating how to build lists with Jetpack Compose using LazyColumn.

## Implementation Details

### Part 1: Create Composable Task List with LazyColumn

#### Step 1: Add Compose dependencies and create Fragment with Compose

**Compose dependencies in build.gradle (app level):**
```gradle
implementation platform('androidx.compose:compose-bom:2024.01.00')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.ui:ui-tooling-preview'
implementation 'androidx.lifecycle:lifecycle-runtime-compose:2.7.0'
debugImplementation 'androidx.compose.ui:ui-tooling'
```

**TaskListComposeFragment implementation:**
```kotlin
class TaskListComposeFragment : Fragment() {
    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    TaskListScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { taskId ->
                            findNavController().navigate(
                                TaskListComposeFragmentDirections.actionListToDetail(taskId)
                            )
                        }
                    )
                }
            }
        }
    }
}
```

**TaskListScreen composable:**
```kotlin
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Tasks") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Will add in next TODO */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        TaskList(
            tasks = tasks,
            onTaskClick = onNavigateToDetail,
            onToggleComplete = { task ->
                viewModel.toggleCompletion(task.id, !task.completed)
            },
            onToggleFavorite = { task ->
                viewModel.toggleFavorite(task.id, !task.isFavorite)
            },
            onDeleteTask = { task ->
                viewModel.deleteTask(task.id)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
```

**Key concepts:**
- ComposeView embeds Compose UI in Fragment
- `activityViewModels()` shares ViewModel with other fragments
- Navigation still uses Navigation Component with Safe Args
- `collectAsStateWithLifecycle()` observes StateFlow with lifecycle awareness
- Material3 Scaffold provides app bar and FAB structure

#### Step 2: Implement LazyColumn with task items

**TaskList composable with LazyColumn:**
```kotlin
@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Int) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onToggleFavorite: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        EmptyState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = tasks,
                key = { task -> task.id }
            ) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) },
                    onToggleComplete = { onToggleComplete(task) },
                    onToggleFavorite = { onToggleFavorite(task) },
                    onDeleteTask = { onDeleteTask(task) }
                )
                HorizontalDivider()
            }
        }
    }
}

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
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
```

**Key concepts:**
- LazyColumn renders only visible items for performance
- `key = { task -> task.id }` enables stable item identity for animations
- Empty state shows when no tasks exist

#### Step 3: Create TaskItem composable with Material3 components

**TaskItem implementation:**
```kotlin
@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick)
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
```

**Key concepts:**
- Material3 components automatically handle theming
- Completed tasks show strikethrough and reduced opacity
- Favorite icon changes based on state

### Part 2: Add Swipe-to-Dismiss and Animations

#### Step 4: Implement swipe-to-dismiss with SwipeToDismissBox

**DismissibleTaskItem implementation:**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleTaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToEnd ||
                dismissValue == DismissValue.DismissedToStart) {
                onDeleteTask()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd, DismissValue.DismissedToStart -> {
                        MaterialTheme.colorScheme.error
                    }
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "backgroundColorAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        modifier = modifier
    ) {
        Surface {
            TaskItem(
                task = task,
                onTaskClick = onTaskClick,
                onToggleComplete = onToggleComplete,
                onToggleFavorite = onToggleFavorite,
                onDeleteTask = onDeleteTask
            )
        }
    }
}
```

**Update LazyColumn to use DismissibleTaskItem:**
```kotlin
items(
    items = tasks,
    key = { task -> task.id }
) { task ->
    DismissibleTaskItem(
        task = task,
        onTaskClick = { onTaskClick(task.id) },
        onToggleComplete = { onToggleComplete(task) },
        onToggleFavorite = { onToggleFavorite(task) },
        onDeleteTask = { onDeleteTask(task) }
    )
    HorizontalDivider()
}
```

**Key concepts:**
- SwipeToDismissBox provides native swipe gesture handling
- Background animates to red when swiping to delete
- Dismissal confirmed only when swipe completes

#### Step 5: Add list animations and task dialog

**Updated TaskListScreen with animations and dialog:**
```kotlin
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Tasks") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = tasks,
                key = { task -> task.id }
            ) { task ->
                DismissibleTaskItem(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) },
                    onToggleComplete = { task ->
                        viewModel.toggleCompletion(task.id, !task.completed)
                    },
                    onToggleFavorite = { task ->
                        viewModel.toggleFavorite(task.id, !task.isFavorite)
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task.id)
                    },
                    modifier = Modifier.animateItemPlacement()
                )
                HorizontalDivider()
            }
        }
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
```

**Key concepts:**
- `animateItemPlacement()` provides automatic reordering animations
- Dialog state managed with `remember { mutableStateOf() }`
- TextField validation enables/disables Add button

## Solution Summary

The solution demonstrates:

1. **ComposeView Integration:** Embedding Compose UI within existing Fragment-based architecture
2. **LazyColumn:** Efficient list rendering with only visible items
3. **Material3 Components:** Modern UI with automatic theming
4. **State Management:** Using StateFlow with `collectAsStateWithLifecycle()`
5. **Swipe Gestures:** SwipeToDismissBox for intuitive deletion
6. **Animations:** Item placement animations and color transitions
7. **Dialog Handling:** State-driven dialog management with validation

This approach allows incremental adoption of Compose while maintaining Navigation Component and existing architecture.