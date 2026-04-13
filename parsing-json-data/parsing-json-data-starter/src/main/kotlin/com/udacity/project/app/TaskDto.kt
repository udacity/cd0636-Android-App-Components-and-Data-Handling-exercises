package com.udacity.project.app

/**
 * Data Transfer Object for Task API responses.
 * Matches the structure from JSONPlaceholder API.
 *
 * Example JSON:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "delectus aut autem",
 *   "completed": false
 * }
 */
data class TaskDto(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)