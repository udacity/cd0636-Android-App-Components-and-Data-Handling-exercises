package com.udacity.project.app

/**
 * Data class representing a task in the Task Manager app.
 * @param id Unique identifier for the task
 * @param title The task description
 * @param completed Whether the task has been completed
 */
data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean = false
)