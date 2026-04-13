package com.udacity.project.app

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for JSONPlaceholder todos endpoint.
 * Retrofit will generate the implementation at runtime.
 *
 * TODO 1.3: Complete this interface with API endpoint definitions
 */
interface TaskApiService {

    // TODO 1.3a: Add getTasks() function
    // Create a suspend function that:
    // 1. Has @GET annotation with "todos" endpoint
    // 2. Returns List<TaskDto>
    // 3. Is a suspend function (for coroutine support)
    //
    // Example:
    // @GET("todos")
    // suspend fun getTasks(): List<TaskDto>

    // TODO 1.3b: Add getTask() function
    // Create a suspend function that:
    // 1. Has @GET annotation with "todos/{id}" endpoint
    // 2. Takes taskId: Int parameter with @Path("id") annotation
    // 3. Returns TaskDto
    // 4. Is a suspend function (for coroutine support)
    //
    // Example:
    // @GET("todos/{id}")
    // suspend fun getTask(@Path("id") taskId: Int): TaskDto
}