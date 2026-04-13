package com.udacity.project.app

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object for Retrofit configuration.
 * Complete the TODOs to set up Retrofit with Moshi converter and logging.
 */
object TasksService {

    // TODO 1.1: Define BASE_URL constant
    // Create a private const val BASE_URL with value "https://jsonplaceholder.typicode.com/"
    // IMPORTANT: URL must end with forward slash (/)
    // Hint: private const val BASE_URL = "..."

    // Moshi instance with Kotlin support (Pre-configured)
    // This enables proper serialization/deserialization of Kotlin data classes
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // TODO 1.2: Create HttpLoggingInterceptor
    // Create a private val loggingInterceptor that:
    // 1. Creates HttpLoggingInterceptor instance
    // 2. Sets level to HttpLoggingInterceptor.Level.BODY in apply block
    //
    // Hint: private val loggingInterceptor = HttpLoggingInterceptor().apply {
    //     level = HttpLoggingInterceptor.Level.BODY
    // }

    // TODO 1.2: Create OkHttpClient with interceptor
    // Create a private val okHttpClient using OkHttpClient.Builder():
    // 1. Add the logging interceptor: .addInterceptor(loggingInterceptor)
    // 2. Set connect timeout: .connectTimeout(30, TimeUnit.SECONDS)
    // 3. Set read timeout: .readTimeout(30, TimeUnit.SECONDS)
    // 4. Call .build()
    //
    // Hint: private val okHttpClient = OkHttpClient.Builder()
    //     .addInterceptor(loggingInterceptor)
    //     .connectTimeout(30, TimeUnit.SECONDS)
    //     .readTimeout(30, TimeUnit.SECONDS)
    //     .build()

    // TODO 1.1: Build Retrofit instance
    // Create a lazy val retrofit using Retrofit.Builder():
    // 1. Set base URL: .baseUrl(BASE_URL)
    // 2. Add OkHttpClient: .client(okHttpClient) - from TODO 1.2
    // 3. Add Moshi converter: .addConverterFactory(MoshiConverterFactory.create(moshi))
    // 4. Call .build()
    //
    // Use lazy initialization so Retrofit is only created when first accessed
    //
    // Hint: val retrofit: Retrofit by lazy {
    //     Retrofit.Builder()
    //         .baseUrl(BASE_URL)
    //         .client(okHttpClient)
    //         .addConverterFactory(MoshiConverterFactory.create(moshi))
    //         .build()
    // }

    // TODO 1.3: Create API service instance
    // Create a lazy val api that generates TaskApiService implementation:
    // Use retrofit.create() with TaskApiService::class.java
    //
    // Hint: val api: TaskApiService by lazy {
    //     retrofit.create(TaskApiService::class.java)
    // }
}