package com.udacity.project.app.data.api

import android.content.Context
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object CountriesService {
    private const val BASE_URL = "https://restcountries.com/"
    private const val CACHE_SIZE = 10L * 1024 * 1024

    private var cache: Cache? = null

    fun init(context: Context) {
        if (cache == null) {
            cache = Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val moshi = Moshi.Builder().build()

    val retrofit: Retrofit by lazy {
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
