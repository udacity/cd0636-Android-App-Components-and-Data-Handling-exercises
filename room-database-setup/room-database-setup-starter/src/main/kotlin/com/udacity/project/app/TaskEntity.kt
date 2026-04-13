package com.udacity.project.app

/**
 * TODO 2: Create TaskEntity with Room annotations
 *
 * 1. Add @Entity annotation with tableName = "tasks"
 * 2. Add @PrimaryKey annotation to id field
 * 3. Add @ColumnInfo annotations to map field names to column names
 * 4. Add conversion functions: toDomainModel() and Task.toEntity()
 */
data class TaskEntity(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// TODO 2.1: Add extension functions for conversion
// fun TaskEntity.toDomainModel(): Task { ... }
// fun Task.toEntity(): TaskEntity { ... }