package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FavoritesListFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTaskFab: FloatingActionButton
    private lateinit var totalTasksTextView: TextView
    private lateinit var completedTasksTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView)
        addTaskFab = view.findViewById(R.id.addTaskFab)
        totalTasksTextView = view.findViewById(R.id.totalTasksTextView)
        completedTasksTextView = view.findViewById(R.id.completedTasksTextView)
    }

    private fun setupRecyclerView() {
        // TODO 5.5: Set up TaskAdapter with navigation to detail screen
        // Use Safe Args: FavoritesListFragmentDirections.actionFavoritesToDetail(task.id)
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                // Add navigation to detail screen here
            },
            onTaskToggled = { taskId ->
                viewModel.toggleTaskCompletion(taskId)
            },
            onTaskDeleted = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )
        tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupClickListeners() {
        // TODO 5.1: Set up FAB click listener to navigate to AddTaskFragment
        // Use Safe Args: FavoritesListFragmentDirections.actionFavoritesToAddTask()
        addTaskFab.setOnClickListener {
            // Add navigation code here
        }
    }

    private fun observeViewModel() {
        // TODO 5.2: Observe tasks and filter to show only favorites
        // Filter tasks where isFavorite == true
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            // Add filtering code here
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