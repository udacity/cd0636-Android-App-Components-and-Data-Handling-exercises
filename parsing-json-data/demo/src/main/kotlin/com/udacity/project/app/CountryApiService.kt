package com.udacity.project.app

import retrofit2.http.GET

/**
 * Retrofit API service interface for REST Countries API.
 * Defines HTTP operations using Retrofit annotations.
 */
interface CountryApiService {
    @GET("v4/all?fields=name,capital,currencies")
    suspend fun getCountries(): List<CountryResponse>
}
