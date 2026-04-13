package com.udacity.project.app.data.api

import retrofit2.http.GET

interface CountryApiService {
    @GET("v4/all?fields=name,capital,currencies")
    suspend fun getCountries(): List<CountryResponse>
}
