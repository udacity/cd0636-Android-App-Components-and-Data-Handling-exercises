package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * REFERENCE: RecyclerView Adapter from previous exercise
 *
 * Compare this with your LazyColumn implementation:
 *
 * RecyclerView Approach (this file):
 * ✓ Adapter class with onCreateViewHolder, onBindViewHolder, getItemCount
 * ✓ ViewHolder class with findViewById and bind()
 * ✓ updateTasks() + notifyDataSetChanged() to refresh UI
 * ✓ XML layout for item views (item_task.xml)
 *
 * LazyColumn Approach (what you'll build):
 * ✓ No adapter class - use items() DSL directly
 * ✓ No ViewHolder - use composable functions
 * ✓ No manual refresh - automatic recomposition
 * ✓ No XML - declare UI with composables
 *
 * This file is kept for reference. You won't need to modify it.
 */
class TaskAdapter(
    private val onTaskToggled: (Int) -> Unit,
    private val onTaskDeleted: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.completed

            taskCheckbox.setOnClickListener {
                onTaskToggled(task.id)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task.id)
            }
        }
    }
}