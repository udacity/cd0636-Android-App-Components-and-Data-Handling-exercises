package com.udacity.project.app

/**
 * Data Transfer Object for JSONPlaceholder API responses.
 * Matches the structure from the /todos endpoint.
 *
 * Example JSON:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "delectus aut autem",
 *   "completed": false
 * }
 */
data class TodoDto(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)