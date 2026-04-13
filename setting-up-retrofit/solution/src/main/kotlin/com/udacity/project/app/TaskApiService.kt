package com.udacity.project.app

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for JSONPlaceholder todos endpoint.
 * Defines HTTP operations using Retrofit annotations.
 */
interface TaskApiService {
    /**
     * Fetches all tasks from the API.
     * GET https://jsonplaceholder.typicode.com/todos
     *
     * @return List of TaskDto objects from the API
     */
    @GET("todos")
    suspend fun getTasks(): List<TaskDto>

    /**
     * Fetches a single task by ID.
     * GET https://jsonplaceholder.typicode.com/todos/{id}
     *
     * @param taskId The ID of the task to fetch
     * @return TaskDto object for the specified task
     */
    @GET("todos/{id}")
    suspend fun getTask(@Path("id") taskId: Int): TaskDto
}