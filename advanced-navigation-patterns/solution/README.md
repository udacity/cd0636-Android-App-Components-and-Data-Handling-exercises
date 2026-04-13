# Task Manager with Bottom Navigation and Multiple Back Stacks - Solution

## Overview

This is the complete solution for Exercise 11, demonstrating advanced navigation patterns with bottom navigation and multiple independent back stacks. The application allows users to switch between tabs (All Tasks, Favorites, Completed) while maintaining separate navigation history for each tab. It includes FAB navigation to a shared AddTaskFragment and real-time updates across all tabs using a shared ViewModel.

## Exercise Instructions

This section provides detailed step-by-step instructions for completing each TODO in the exercise. Follow these steps to understand how the solution was implemented.

### Part 1: Configure Navigation Graphs (TODOs 1.1-1.9)

#### Step 1.1: Add allTasksListFragment to nav_all_tasks.xml

Open `nav_all_tasks.xml` and add the fragment with its actions:

```xml
<fragment
    android:id="@+id/allTasksListFragment"
    android:name="com.udacity.project.app.AllTasksListFragment"
    android:label="All Tasks"
    tools:layout="@layout/fragment_task_list">
    <action
        android:id="@+id/action_list_to_detail"
        app:destination="@id/taskDetailFragment" />
    <action
        android:id="@+id/action_list_to_add_task"
        app:destination="@id/addTaskFragment" />
</fragment>
```

**Why?** This defines the start destination for the All Tasks tab and creates navigation actions to both the detail screen and add task screen. Actions with Safe Args generate type-safe navigation methods.

#### Step 1.2: Add taskDetailFragment to nav_all_tasks.xml

Add the detail fragment with an argument:

```xml
<fragment
    android:id="@+id/taskDetailFragment"
    android:name="com.udacity.project.app.TaskDetailFragment"
    android:label="Task Detail"
    tools:layout="@layout/fragment_task_detail">
    <argument
        android:name="taskId"
        app:argType="integer" />
</fragment>
```

**Why?** The argument defines what data needs to be passed when navigating to this screen. Safe Args will generate `TaskDetailFragmentArgs` class to access this argument safely.

#### Step 1.3: Add addTaskFragment to nav_all_tasks.xml

Add the add task fragment:

```xml
<fragment
    android:id="@+id/addTaskFragment"
    android:name="com.udacity.project.app.AddTaskFragment"
    android:label="Add Task"
    tools:layout="@layout/fragment_add_task" />
```

**Why?** This shared destination can be accessed from the list screen via the FAB. Even though it's in multiple nav graphs, it's the same fragment implementation.

#### Steps 1.4-1.6: Repeat for nav_favorites.xml

Follow the same pattern for `nav_favorites.xml` with `favoritesListFragment` as the start destination:

```xml
<navigation android:id="@+id/nav_favorites" app:startDestination="@id/favoritesListFragment">
    <fragment android:id="@+id/favoritesListFragment" ... />
    <fragment android:id="@+id/taskDetailFragment" ... />
    <fragment android:id="@+id/addTaskFragment" ... />
</navigation>
```

**Why?** Each tab gets its own navigation graph with its own start destination, allowing independent back stacks.

#### Steps 1.7-1.9: Repeat for nav_completed.xml

Follow the same pattern for `nav_completed.xml` with `completedListFragment` as the start destination.

**Why?** Three separate navigation graphs enable the framework to automatically manage three independent back stacks.

**After completing Step 1: Build the project to generate Safe Args classes.**

---

### Part 2: Create Bottom Navigation Menu (TODOs 2.1-2.3)

#### Step 2.1: Add "All Tasks" menu item

Open `bottom_nav_menu.xml` and add:

```xml
<item
    android:id="@+id/nav_all_tasks"
    android:title="All"
    android:icon="@drawable/ic_list" />
```

**Why?** The menu item ID must match the navigation graph ID (`@+id/nav_all_tasks`). This is how NavigationUI automatically switches between navigation graphs when tabs are selected.

#### Steps 2.2-2.3: Add remaining menu items

Add the Favorites and Completed items:

```xml
<item
    android:id="@+id/nav_favorites"
    android:title="Favorites"
    android:icon="@drawable/ic_star" />
<item
    android:id="@+id/nav_completed"
    android:title="Done"
    android:icon="@drawable/ic_check" />
```

**Why?** Each menu item corresponds to a navigation graph. When a user taps a tab, NavigationUI uses the matching ID to load the corresponding navigation graph.

---

### Part 3: Configure MainActivity with NavigationUI (TODOs 3.1-3.4)

#### Step 3.1: Get NavHostFragment and NavController

In `MainActivity.kt`, add to `onCreate()`:

```kotlin
val navHostFragment = supportFragmentManager
    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
val navController = navHostFragment.navController
```

**Why?** The NavController manages navigation within the NavHostFragment. We need it to configure NavigationUI.

#### Step 3.2: Set up BottomNavigationView with NavController

Add these lines:

```kotlin
val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
bottomNav.setupWithNavController(navController)
```

**Why?** `setupWithNavController()` is the key method that enables multiple back stack support. It automatically:
- Switches navigation graphs when tabs are selected
- Saves each tab's back stack when switching away
- Restores back stack when returning to a tab
- Handles configuration changes

#### Step 3.3: Configure AppBar with top-level destinations

Add these lines:

```kotlin
val appBarConfiguration = AppBarConfiguration(
    setOf(R.id.nav_all_tasks, R.id.nav_favorites, R.id.nav_completed)
)
setupActionBarWithNavController(navController, appBarConfiguration)
```

**Why?** Top-level destinations don't show the up button in the ActionBar. All three tabs are top-level; only deeper screens (detail, add task) show the up button.

#### Step 3.4: Implement up navigation

Override the method:

```kotlin
override fun onSupportNavigateUp(): Boolean {
    val navHostFragment = supportFragmentManager
        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    val navController = navHostFragment.navController
    return navController.navigateUp() || super.onSupportNavigateUp()
}
```

**Why?** This handles the up button press in the ActionBar, navigating back within the current tab's navigation graph.

**After completing Step 3: Run the app. Bottom navigation should work with automatic back stack management.**

---

### Part 4: Implement FAB Navigation (TODOs 4.1-4.2)

#### Step 4.1: Set up FAB click listener in AllTasksListFragment

In `AllTasksListFragment.kt`, complete `setupClickListeners()`:

```kotlin
private fun setupClickListeners() {
    addTaskFab.setOnClickListener {
        val action = AllTasksListFragmentDirections.actionListToAddTask()
        findNavController().navigate(action)
    }
}
```

**Why?** Safe Args generates the `AllTasksListFragmentDirections` class from the navigation graph. The `actionListToAddTask()` method creates a type-safe action object for navigation.

#### Step 4.2: Set up navigation to detail screen

In `setupRecyclerView()`:

```kotlin
taskAdapter = TaskAdapter(
    onTaskClicked = { task ->
        val action = AllTasksListFragmentDirections.actionListToDetail(task.id)
        findNavController().navigate(action)
    },
    // ... other callbacks
)
```

**Why?** `actionListToDetail(task.id)` passes the task ID as an argument. Safe Args ensures we can't navigate without providing the required argument.

**After completing Step 4: Test FAB and item navigation from All Tasks tab.**

---

### Part 5: Create Tab-Specific List Fragments (TODOs 5.1-5.6)

#### Step 5.1: FAB navigation in FavoritesListFragment

In `FavoritesListFragment.kt`, add:

```kotlin
private fun setupClickListeners() {
    addTaskFab.setOnClickListener {
        val action = FavoritesListFragmentDirections.actionFavoritesToAddTask()
        findNavController().navigate(action)
    }
}
```

**Why?** Each fragment uses its own Directions class with actions defined in its navigation graph. This maintains proper back stack behavior per tab.

#### Step 5.2: Filter tasks to show only favorites

In `observeViewModel()`:

```kotlin
viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
    val favoriteTasks = tasks.filter { it.isFavorite }
    taskAdapter.updateTasks(favoriteTasks)
}
```

**Why?** By filtering the task list, this tab only shows favorites. The filtering happens in the UI layer; the ViewModel holds all tasks.

#### Step 5.3-5.4: Repeat for CompletedListFragment

Follow the same pattern, filtering for completed tasks:

```kotlin
private fun setupClickListeners() {
    addTaskFab.setOnClickListener {
        val action = CompletedListFragmentDirections.actionCompletedToAddTask()
        findNavController().navigate(action)
    }
}

private fun observeViewModel() {
    viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
        val completedTasks = tasks.filter { it.completed }
        taskAdapter.updateTasks(completedTasks)
    }
}
```

**Why?** Each tab filters the shared data differently, providing different views of the same dataset.

#### Steps 5.5-5.6: Navigation to detail screen

In both fragments, add navigation in `setupRecyclerView()`:

```kotlin
taskAdapter = TaskAdapter(
    onTaskClicked = { task ->
        // FavoritesListFragment:
        val action = FavoritesListFragmentDirections.actionFavoritesToDetail(task.id)
        findNavController().navigate(action)

        // OR CompletedListFragment:
        val action = CompletedListFragmentDirections.actionCompletedToDetail(task.id)
        findNavController().navigate(action)
    },
    // ...
)
```

**Why?** Each fragment uses its own navigation actions, ensuring navigation stays within its tab's back stack.

**After completing Step 5: Test navigation in all three tabs. Each maintains independent back stack.**

---

### Part 6: Add Favorite Toggle Feature (TODOs 6.1-6.3)

#### Step 6.1: Get taskId from Safe Args in TaskDetailFragment

In `TaskDetailFragment.kt`, replace the placeholder in `onViewCreated()`:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initializeViews(view)

    val args: TaskDetailFragmentArgs by navArgs()
    val taskId = args.taskId

    setupUI(taskId)
    observeTask(taskId)
}
```

**Why?** Safe Args generates `TaskDetailFragmentArgs` from the navigation graph's argument definition. Using `by navArgs()` provides type-safe access to the passed `taskId` argument. This replaces the placeholder value `-1` with the actual task ID from navigation.

#### Step 6.2: Set up favorite button in TaskDetailFragment

In `TaskDetailFragment.kt`, add to `setupUI()`:

```kotlin
favoriteButton.setOnClickListener {
    viewModel.toggleFavorite(taskId)
}
```

**Why?** The button calls the ViewModel method to update data. Because all fragments observe the same ViewModel, the Favorites tab will automatically update when this changes.

#### Step 6.3: Implement toggleFavorite in TaskViewModel

In `TaskViewModel.kt`, implement the method:

```kotlin
fun toggleFavorite(taskId: Int) {
    val currentTasks = tasks.value ?: emptyList()
    val updatedTasks = currentTasks.map { task ->
        if (task.id == taskId) task.copy(isFavorite = !task.isFavorite)
        else task
    }
    savedStateHandle["tasks"] = updatedTasks
}
```

**Why?** This method:
1. Gets the current task list
2. Maps through tasks, toggling `isFavorite` for the matching task
3. Saves the updated list to SavedStateHandle, which triggers LiveData observers
4. All fragments observing `tasks` automatically receive the update

**After completing Step 6: Toggle favorite and verify the Favorites tab updates in real-time.**

---

## Key Concepts Demonstrated

### Multiple Back Stacks
Each bottom navigation tab maintains its own navigation history:
- Navigate deep in one tab (List → Detail)
- Switch to another tab
- That tab's back stack is empty (or has its own history)
- Press back to navigate within current tab
- Switch back to first tab
- Previous navigation state is restored

### Shared Destinations
`TaskDetailFragment` and `AddTaskFragment` appear in all three navigation graphs:
- Same fragment implementation
- Different navigation paths depending on source
- Back navigation respects the origin tab

### NavigationUI Automation
`setupWithNavController()` automatically:
- Switches navigation graphs on tab selection
- Saves/restores back stacks
- Survives configuration changes
- Handles deep links

### Shared ViewModel
Using `by activityViewModels()` instead of `by viewModels()`:
- Creates single ViewModel instance scoped to Activity
- All fragments observe same data
- Changes in one fragment instantly visible in others
- Enables real-time cross-fragment updates

### SavedStateHandle
Persists data across:
- Configuration changes (rotation)
- Process death (low memory)
- System-initiated Activity recreation

---

## Testing the Solution

### Test Multiple Back Stacks:

1. Open All Tasks tab → Tap task → See detail (back stack: List → Detail)
2. Tap Favorites tab (empty back stack)
3. Tap a favorite → See detail (back stack: List → Detail)
4. Tap All Tasks tab again
5. Press back → Returns to All Tasks detail (not Favorites!)
6. Press back → Returns to All Tasks list
7. Tap Favorites tab
8. Press back → Returns to Favorites list (independent back stack)

### Test Shared Destinations:

1. Open All Tasks → Tap FAB → AddTaskFragment
2. Add task → Returns to All Tasks
3. Tap Completed → Tap FAB → Same AddTaskFragment
4. Cancel → Returns to Completed (not All Tasks!)

### Test Real-Time Updates:

1. Open All Tasks → Tap task
2. Tap "Add to Favorites" button
3. Press back to All Tasks list
4. Tap Favorites tab
5. Verify task appears instantly (no refresh needed)

---

## Architecture Summary

```
MainActivity
├── NavHostFragment (with multiple back stacks)
│   ├── nav_all_tasks
│   │   ├── AllTasksListFragment → TaskDetailFragment
│   │   └── AllTasksListFragment → AddTaskFragment
│   ├── nav_favorites
│   │   ├── FavoritesListFragment → TaskDetailFragment
│   │   └── FavoritesListFragment → AddTaskFragment
│   └── nav_completed
│       ├── CompletedListFragment → TaskDetailFragment
│       └── CompletedListFragment → AddTaskFragment
└── BottomNavigationView (controls graph switching)

TaskViewModel (shared across all fragments)
└── SavedStateHandle (persists data)
```

---

## Common Issues

**Issue**: Safe Args classes not found
**Solution**: Build project after creating navigation graphs

**Issue**: Back stack not saved when switching tabs
**Solution**: Ensure menu IDs match navigation graph IDs exactly

**Issue**: Changes in one tab don't appear in others
**Solution**: Use `by activityViewModels()` not `by viewModels()`

**Issue**: Up button appears on tab screens
**Solution**: Include all tab IDs in `AppBarConfiguration`