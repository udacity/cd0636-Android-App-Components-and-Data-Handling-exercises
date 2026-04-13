package com.udacity.project.app

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object for Retrofit configuration and API service creation.
 * Configured with Moshi and KSP-generated adapters for JSON parsing.
 */
object CountriesService {
    private const val BASE_URL = "https://restcountries.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Moshi instance for JSON parsing.
     *
     * When using @JsonClass(generateAdapter = true) with KSP,
     * you do NOT need KotlinJsonAdapterFactory — KSP generates adapters
     * at compile time, which is faster than reflection.
     */
    private val moshi = Moshi.Builder()
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: CountryApiService by lazy {
        retrofit.create(CountryApiService::class.java)
    }
}
