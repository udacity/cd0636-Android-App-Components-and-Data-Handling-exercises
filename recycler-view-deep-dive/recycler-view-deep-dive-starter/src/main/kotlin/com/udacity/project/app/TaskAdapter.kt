package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Basic RecyclerView.Adapter implementation.
 *
 * Complete Part 1 TODOs to convert to ListAdapter with DiffUtil.
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

    /**
     * TODO 1.1: Create TaskDiffCallback
     *
     * Create a nested class that extends DiffUtil.ItemCallback<Task>
     * Implement:
     * - areItemsTheSame(): Compare tasks by ID
     * - areContentsTheSame(): Compare entire Task objects
     */

    /**
     * TODO 1.2: Convert to ListAdapter
     *
     * Change class signature to extend ListAdapter instead of RecyclerView.Adapter:
     * - Extend: ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback())
     * - Remove the tasks property and updateTasks() method
     * - In onBindViewHolder, use getItem(position) instead of tasks[position]
     * - Remove getItemCount() override (ListAdapter handles this)
     */

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
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.completed
            updateFavoriteIcon(task.isFavorite)

            taskCheckbox.setOnClickListener {
                onTaskToggled(task.id)
            }

            deleteButton.setOnClickListener {
                onTaskDeleted(task.id)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.star_big_on
                else android.R.drawable.star_big_off
            )
        }
    }
}