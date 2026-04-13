package com.udacity.project.app

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for JSONPlaceholder todos endpoint.
 * Defines HTTP operations using Retrofit annotations.
 */
interface TodoApiService {
    @GET("todos")
    suspend fun getTodos(): List<TodoDto>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") todoId: Int): TodoDto

}