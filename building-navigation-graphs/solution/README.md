# Task Manager with Navigation Component - Solution

## Overview

This is the complete solution for the Task Manager with Navigation Component exercise that demonstrates fragment-based navigation using Navigation Graph and Safe Args. The application uses ViewModel, LiveData, and SavedStateHandle for state management and shows how Navigation Component simplifies multi-screen navigation with compile-time type safety.

## Exercise Instructions

This section provides detailed step-by-step instructions for completing each TODO in the exercise.

### Part 1: Completing nav_graph.xml

#### Step 1: Define destinations and action

Open `res/navigation/nav_graph.xml` and create the navigation structure:

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/taskListFragment">

    <fragment
        android:id="@+id/taskListFragment"
        android:name="com.udacity.taskmanager.TaskListFragment"
        android:label="Task List"
        tools:layout="@layout/fragment_task_list">

        <action
            android:id="@+id/action_list_to_detail"
            app:destination="@id/taskDetailFragment" />
    </fragment>

    <fragment
        android:id="@+id/taskDetailFragment"
        android:name="com.udacity.taskmanager.TaskDetailFragment"
        android:label="Task Detail"
        tools:layout="@layout/fragment_task_detail" />

</navigation>
```

**Key Points:**
- `app:startDestination` specifies which fragment shows first
- Each `<fragment>` represents a destination in the navigation graph
- `<action>` defines a navigation path from one destination to another
- `android:name` must match your actual fragment class package and name

#### Step 2: Add Safe Args argument

Add the `taskId` argument to `taskDetailFragment`:

```xml
<fragment
    android:id="@+id/taskDetailFragment"
    android:name="com.udacity.taskmanager.TaskDetailFragment"
    android:label="Task Detail"
    tools:layout="@layout/fragment_task_detail">

    <argument
        android:name="taskId"
        app:argType="integer" />
</fragment>
```

**After adding the argument:**
1. Build → Rebuild Project (or Ctrl+F9 / Cmd+F9)
2. This generates two classes:
   - `TaskListFragmentDirections` - contains type-safe methods for navigation actions
   - `TaskDetailFragmentArgs` - contains type-safe properties for accessing arguments

**Safe Args Benefits:**
- Compile-time type checking (passing String where Int expected = build error)
- Auto-completion for navigation actions and arguments
- No manual Bundle manipulation
- Refactoring support (rename argument updates all usages)

### Part 2: Completing Navigation Implementation

#### Step 1: Navigate to detail with Safe Args (TaskListFragment.kt)

Open `TaskListFragment.kt` and update the RecyclerView item click handler:

```kotlin
import androidx.navigation.fragment.findNavController

class TaskListFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                // TODO: Navigate to detail screen
                val action = TaskListFragmentDirections.actionListToDetail(task.id)
                findNavController().navigate(action)
            },
            onTaskToggled = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )

        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.updateTasks(tasks)
        }

        viewModel.totalTaskCount.observe(viewLifecycleOwner) { count ->
            totalTasksTextView.text = "Total: $count"
        }

        viewModel.completedTaskCount.observe(viewLifecycleOwner) { count ->
            completedTasksTextView.text = "Completed: $count"
        }
    }
}
```

**Key Points:**
- `TaskListFragmentDirections` is generated from the navigation graph
- `actionListToDetail(task.id)` is a type-safe method generated from the `action_list_to_detail` action
- The method parameter matches the argument type (Int for `taskId`)
- `findNavController()` returns the NavController that manages this fragment

#### Step 2: Retrieve arguments in detail fragment (TaskDetailFragment.kt)

Open `TaskDetailFragment.kt` and retrieve the Safe Args:

```kotlin
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class TaskDetailFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Retrieve taskId from Safe Args
        val taskId = args.taskId

        setupUI(taskId)
        observeTask(taskId)
    }

    private fun observeTask(taskId: Int) {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let { displayTask(it) }
        }
    }

    private fun displayTask(task: Task) {
        taskTitleTextView.text = task.title
        taskStatusTextView.text = if (task.completed) {
            "Status: Completed"
        } else {
            "Status: Not Completed"
        }
    }

    private fun setupUI(taskId: Int) {
        toggleButton.setOnClickListener {
            viewModel.toggleTaskCompletion(taskId)
        }

        deleteButton.setOnClickListener {
            // Implementation in next step
        }
    }
}
```

**Key Points:**
- `by navArgs()` is a property delegate that retrieves arguments lazily
- `TaskDetailFragmentArgs` is generated from the `<argument>` in nav_graph.xml
- `args.taskId` provides type-safe access to the Int argument
- `by activityViewModels()` shares the ViewModel with TaskListFragment
- The ViewModel survives navigation between fragments

#### Step 3: Navigate back to list (TaskDetailFragment.kt)

Complete the delete button handler with navigation back:

```kotlin
private fun setupUI(taskId: Int) {
    toggleButton.setOnClickListener {
        viewModel.toggleTaskCompletion(taskId)
    }

    deleteButton.setOnClickListener {
        // TODO: Delete and navigate back
        viewModel.deleteTask(taskId)
        findNavController().navigateUp()
    }
}
```

**Alternative Navigation Methods:**
```kotlin
// Option 1: navigateUp() - goes to previous destination
findNavController().navigateUp()

// Option 2: popBackStack() - same as navigateUp() in this case
findNavController().popBackStack()

// Option 3: Navigate to specific destination by ID
findNavController().navigate(R.id.taskListFragment)
```

**Why navigateUp() is preferred:**
- Respects the navigation graph structure
- Handles Up button and back button consistently
- Works with deep links and nested graphs
- More maintainable than hardcoding destination IDs

## Complete File Examples

### nav_graph.xml (Complete)

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/taskListFragment">

    <fragment
        android:id="@+id/taskListFragment"
        android:name="com.udacity.taskmanager.TaskListFragment"
        android:label="Task List"
        tools:layout="@layout/fragment_task_list">

        <action
            android:id="@+id/action_list_to_detail"
            app:destination="@id/taskDetailFragment" />
    </fragment>

    <fragment
        android:id="@+id/taskDetailFragment"
        android:name="com.udacity.taskmanager.TaskDetailFragment"
        android:label="Task Detail"
        tools:layout="@layout/fragment_task_detail">

        <argument
            android:name="taskId"
            app:argType="integer" />
    </fragment>

</navigation>
```

### TaskListFragment.kt (Navigation Section)

```kotlin
private fun setupRecyclerView() {
    taskAdapter = TaskAdapter(
        onTaskClicked = { task ->
            val action = TaskListFragmentDirections.actionListToDetail(task.id)
            findNavController().navigate(action)
        },
        onTaskToggled = { taskId ->
            viewModel.toggleTaskCompletion(taskId)
        },
        onTaskDeleted = { taskId ->
            viewModel.deleteTask(taskId)
        }
    )

    recyclerView.adapter = taskAdapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
}
```

### TaskDetailFragment.kt (Complete)

```kotlin
class TaskDetailFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    // View references
    private lateinit var taskTitleTextView: TextView
    private lateinit var taskStatusTextView: TextView
    private lateinit var toggleButton: Button
    private lateinit var deleteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        val taskId = args.taskId
        setupUI(taskId)
        observeTask(taskId)
    }

    private fun initializeViews(view: View) {
        taskTitleTextView = view.findViewById(R.id.taskTitleTextView)
        taskStatusTextView = view.findViewById(R.id.taskStatusTextView)
        toggleButton = view.findViewById(R.id.toggleButton)
        deleteButton = view.findViewById(R.id.deleteButton)
    }

    private fun observeTask(taskId: Int) {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let { displayTask(it) }
        }
    }

    private fun displayTask(task: Task) {
        taskTitleTextView.text = task.title
        taskStatusTextView.text = if (task.completed) {
            "Status: Completed"
        } else {
            "Status: Not Completed"
        }

        toggleButton.text = if (task.completed) {
            "Mark as Incomplete"
        } else {
            "Mark as Complete"
        }
    }

    private fun setupUI(taskId: Int) {
        toggleButton.setOnClickListener {
            viewModel.toggleTaskCompletion(taskId)
        }

        deleteButton.setOnClickListener {
            viewModel.deleteTask(taskId)
            findNavController().navigateUp()
        }
    }
}
```

## Verification

To verify your implementation:

1. **Build succeeds** - Safe Args classes generate without errors
2. **Navigate to detail** - Tapping a task shows its detail screen
3. **Correct task shown** - Detail screen displays the tapped task's information
4. **Toggle works** - Toggle button updates the task status
5. **Delete navigates back** - Delete button removes task and returns to list
6. **Updated list** - List reflects changes made in detail screen
7. **Back button works** - System back button returns to list from detail
8. **Type safety** - Changing `task.id` to `task.title` in navigation causes build error

## Architecture Pattern: Shared ViewModel with Navigation

This solution demonstrates the recommended architecture for multi-screen Android apps:

- **Navigation Component** manages fragment transactions and back stack
- **Safe Args** provides compile-time type safety for arguments
- **Shared ViewModel** (via `activityViewModels()`) maintains state across fragments
- **LiveData** enables reactive UI updates across navigation
- **SavedStateHandle** preserves state across process death

**Data Flow:**
```
User taps task → TaskListFragment navigates with taskId →
  → TaskDetailFragment receives taskId via Safe Args →
  → Fragment observes shared ViewModel →
  → User toggles/deletes task →
  → ViewModel updates data →
  → LiveData notifies both fragments →
  → UI updates automatically
```

This pattern scales to complex navigation structures with multiple fragments and nested graphs.