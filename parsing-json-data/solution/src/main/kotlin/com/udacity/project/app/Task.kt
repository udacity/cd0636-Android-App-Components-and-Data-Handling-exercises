package com.udacity.project.app

import java.util.Date

/**
 * Domain model for a Task.
 * Represents a task in the app with all relevant fields.
 */
data class Task(
    val id: Int,
    val title: String,
    val completed: Boolean = false,
    val isFavorite: Boolean = false,
    val createdBy: String = "Unknown",
    val dueDate: Date? = null
)