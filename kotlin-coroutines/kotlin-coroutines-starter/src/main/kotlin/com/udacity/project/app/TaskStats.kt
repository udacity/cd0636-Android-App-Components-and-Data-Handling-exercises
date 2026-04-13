package com.udacity.project.app

/**
 * Data class representing task statistics from the server.
 */
data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val completionRate: Float
)