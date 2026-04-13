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

class AllTasksListFragment : Fragment() {

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
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                val action = AllTasksListFragmentDirections.actionListToDetail(task.id)
                findNavController().navigate(action)
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
        addTaskFab.setOnClickListener {
            val action = AllTasksListFragmentDirections.actionListToAddTask()
            findNavController().navigate(action)
        }
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