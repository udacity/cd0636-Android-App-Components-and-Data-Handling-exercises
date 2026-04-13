package com.udacity.project.app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the tasks table.
 * Separated from domain Task model for clean architecture.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "completed")
    val completed: Boolean,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        completed = completed,
        isFavorite = isFavorite
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        completed = completed,
        isFavorite = isFavorite,
        createdAt = System.currentTimeMillis()
    )
}