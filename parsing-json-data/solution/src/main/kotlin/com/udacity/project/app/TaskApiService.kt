package com.udacity.project.app

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for JSONPlaceholder todos endpoint.
 * Defines HTTP operations using Retrofit annotations.
 * Uses TaskResponse with Moshi annotations for complex JSON parsing.
 */
interface TaskApiService {
    /**
     * Fetches all tasks from the API.
     * GET https://jsonplaceholder.typicode.com/todos
     *
     * @return List of TaskResponse objects from the API
     */
    @GET("todos")
    suspend fun getTasks(): List<TaskResponse>

    /**
     * Fetches a single task by ID.
     * GET https://jsonplaceholder.typicode.com/todos/{id}
     *
     * @param taskId The ID of the task to fetch
     * @return TaskResponse object for the specified task
     */
    @GET("todos/{id}")
    suspend fun getTask(@Path("id") taskId: Int): TaskResponse
}