package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * ListAdapter with DiffUtil for efficient list updates and animations.
 */
class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onToggleComplete: (Task) -> Unit,
    private val onToggleFavorite: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    /**
     * DiffUtil.ItemCallback to calculate the difference between two lists.
     */
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            // Check if items represent the same task (by ID)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            // Check if all fields are the same
            return oldItem == newItem
        }

        /**
         * Return payload to specify what changed for partial updates.
         */
        override fun getChangePayload(oldItem: Task, newItem: Task): Any? {
            return when {
                oldItem.completed != newItem.completed -> PAYLOAD_COMPLETED
                oldItem.isFavorite != newItem.isFavorite -> PAYLOAD_FAVORITE
                else -> null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    /**
     * Bind with payloads for partial updates.
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val task = getItem(position)
            payloads.forEach { payload ->
                when (payload) {
                    PAYLOAD_COMPLETED -> holder.updateCompletion(task.completed)
                    PAYLOAD_FAVORITE -> holder.updateFavorite(task.isFavorite)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckbox.isChecked = task.completed
            favoriteButton.isSelected = task.isFavorite
            updateFavoriteIcon(task.isFavorite)

            itemView.setOnClickListener { onTaskClick(task) }
            taskCheckbox.setOnClickListener { onToggleComplete(task) }
            favoriteButton.setOnClickListener { onToggleFavorite(task) }
            deleteButton.setOnClickListener { /* Handled by swipe gesture */ }
        }

        /**
         * Update only the completion checkbox.
         */
        fun updateCompletion(completed: Boolean) {
            taskCheckbox.isChecked = completed
        }

        /**
         * Update only the favorite icon.
         */
        fun updateFavorite(favorite: Boolean) {
            favoriteButton.isSelected = favorite
            updateFavoriteIcon(favorite)
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.star_big_on
                else android.R.drawable.star_big_off
            )
        }
    }

    companion object {
        private const val PAYLOAD_COMPLETED = "payload_completed"
        private const val PAYLOAD_FAVORITE = "payload_favorite"
    }
}