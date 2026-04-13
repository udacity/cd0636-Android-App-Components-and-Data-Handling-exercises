package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class TaskDetailFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()

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

        // TODO: Step 2 - Retrieve arguments using Safe Args
        val args: TaskDetailFragmentArgs by navArgs()
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
            // TODO: Step 3 - Navigate back after deleting
            viewModel.deleteTask(taskId)
            findNavController().navigateUp()
        }
    }
}