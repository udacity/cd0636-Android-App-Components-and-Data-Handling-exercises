package com.udacity.project.app

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

/**
 * Data Transfer Object for complex Task API responses with Moshi annotations.
 * Demonstrates:
 * - @JsonClass for code generation
 * - @Json for field name mapping (snake_case to camelCase)
 * - Nullable fields for optional data
 * - Nested objects for complex JSON structures
 *
 * Example JSON:
 * {
 *   "id": 1,
 *   "title": "Complete Android project",
 *   "completed": false,
 *   "user": {
 *     "id": 1,
 *     "name": "John Doe",
 *     "email": "john@example.com"
 *   },
 *   "created_at": "2024-01-15T10:30:00Z",
 *   "due_date": "2024-02-01T23:59:59Z"
 * }
 */
@JsonClass(generateAdapter = true)
data class TaskResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "user") val user: UserInfo?,
    @Json(name = "created_at") val createdAt: Date?,
    @Json(name = "due_date") val dueDate: Date?
)

/**
 * Nested user information from API response.
 * Demonstrates handling of nested JSON objects with Moshi.
 */
@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String?
)

/**
 * Extension function to convert API response to domain model.
 * Handles nullable fields with safe defaults using elvis operator.
 */
fun TaskResponse.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        isFavorite = false,
        createdBy = user?.name ?: "Unknown",
        dueDate = dueDate
    )
}