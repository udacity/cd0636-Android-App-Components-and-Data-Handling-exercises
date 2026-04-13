package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val onTodoToggled: (Int) -> Unit,
    private val onTodoDeleted: (Int) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var todos: List<Todo> = emptyList()

    fun updateTodos(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val todoTitle: TextView = itemView.findViewById(R.id.todoTitle)
        private val todoCheckbox: CheckBox = itemView.findViewById(R.id.todoCheckbox)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(todo: Todo) {
            todoTitle.text = todo.title
            todoCheckbox.isChecked = todo.completed

            todoCheckbox.setOnClickListener {
                onTodoToggled(todo.id)
            }

            deleteButton.setOnClickListener {
                onTodoDeleted(todo.id)
            }
        }
    }
}