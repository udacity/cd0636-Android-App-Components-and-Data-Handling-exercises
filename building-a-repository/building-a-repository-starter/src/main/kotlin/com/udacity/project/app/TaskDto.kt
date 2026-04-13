package com.udacity.project.app

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Task API responses from JSONPlaceholder.
 * Matches the structure from JSONPlaceholder API /todos endpoint.
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
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("completed")
    val completed: Boolean,
    @SerializedName("userId")
    val userId: Int
)

/**
 * Extension function to convert API DTO to domain model.
 * Separates API representation from domain model.
 */
fun TaskDto.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed
    )
}