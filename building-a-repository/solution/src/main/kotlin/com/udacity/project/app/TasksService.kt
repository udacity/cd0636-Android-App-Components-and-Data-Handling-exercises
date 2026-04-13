package com.udacity.project.app

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object for Retrofit configuration and API service creation.
 * Provides configured Retrofit instance and TaskApiService.
 */
object TasksService {
    /**
     * Base URL for JSONPlaceholder API.
     * IMPORTANT: Must end with a forward slash (/).
     */
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    /**
     * Logging interceptor for debugging network requests.
     * Logs full request and response bodies to Logcat.
     * Filter Logcat by "OkHttp" to see network logs.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * OkHttpClient with logging interceptor and timeouts.
     * - Logging interceptor: Logs all HTTP traffic for debugging
     * - Connect timeout: 30 seconds to establish connection
     * - Read timeout: 30 seconds to read response data
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Retrofit instance configured with:
     * - Base URL: JSONPlaceholder API
     * - OkHttpClient: With logging and timeouts
     * - Gson Converter: For JSON serialization/deserialization
     *
     * Lazy initialization - created only when first accessed.
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * TaskApiService instance for making API calls.
     * Created from Retrofit using the interface definition.
     *
     * Usage:
     * val tasks = TasksService.api.getTasks()
     */
    val api: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }
}