package com.udacity.project.app

import java.util.Date

/**
 * TODO: Step 1 - Add Moshi annotations for field name mapping
 *
 * This file needs to be completed with Moshi annotations to handle complex JSON parsing.
 *
 * Instructions:
 * 1. Add @JsonClass(generateAdapter = true) to both data classes
 * 2. Add @Json(name = "field_name") annotations to map JSON fields to Kotlin properties
 * 3. Make sure the UserInfo nested class is properly annotated
 *
 * Expected JSON structure:
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

// TODO: Add @JsonClass(generateAdapter = true) annotation
data class TaskResponse(
    // TODO: Add @Json(name = "id") annotation
    val id: Int,

    // TODO: Add @Json(name = "title") annotation
    val title: String,

    // TODO: Add @Json(name = "completed") annotation
    val completed: Boolean,

    // TODO: Step 2 - Add @Json(name = "user") annotation
    // This demonstrates nested object handling
    val user: UserInfo?,

    // TODO: Add @Json(name = "created_at") annotation
    // Note: This will be parsed as Date using our custom DateAdapter
    val createdAt: Date?,

    // TODO: Add @Json(name = "due_date") annotation
    val dueDate: Date?
)

/**
 * TODO: Step 2 - Handle nested JSON objects
 *
 * Nested user information from API response.
 * Add Moshi annotations to handle the nested "user" object in the JSON.
 */
// TODO: Add @JsonClass(generateAdapter = true) annotation
data class UserInfo(
    // TODO: Add @Json(name = "id") annotation
    val id: Int,

    // TODO: Add @Json(name = "name") annotation
    val name: String,

    // TODO: Add @Json(name = "email") annotation
    val email: String?
)

/**
 * TODO: Step 3 - Handle nullable fields and provide defaults
 *
 * Extension function to convert API response to domain model.
 * Instructions:
 * 1. Use the elvis operator (?:) to provide safe defaults for nullable fields
 * 2. For user?.name, provide "Unknown" as the default
 * 3. Handle nullable dates appropriately
 */
fun TaskResponse.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        isFavorite = false,
        // TODO: Use elvis operator to provide "Unknown" default when user is null
        createdBy = user?.name ?: "Unknown",
        dueDate = dueDate
    )
}