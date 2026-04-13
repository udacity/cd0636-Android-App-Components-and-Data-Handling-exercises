package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class AddTaskFragment : Fragment() {

    private val viewModel: TaskViewModel by activityViewModels()

    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        taskTitleEditText = view.findViewById(R.id.taskTitleEditText)
        taskDescriptionEditText = view.findViewById(R.id.taskDescriptionEditText)
        saveButton = view.findViewById(R.id.saveButton)
        cancelButton = view.findViewById(R.id.cancelButton)
    }

    private fun setupClickListeners() {
        saveButton.setOnClickListener {
            val title = taskTitleEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addTask(title)
                findNavController().navigateUp()
            }
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}