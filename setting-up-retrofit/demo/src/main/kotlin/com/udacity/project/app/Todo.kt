package com.udacity.project.app

data class Todo(
    val id: Int,
    val title: String,
    val completed: Boolean = false
)