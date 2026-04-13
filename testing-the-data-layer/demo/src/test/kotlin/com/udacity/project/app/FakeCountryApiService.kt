package com.udacity.project.app

import com.udacity.project.app.data.api.CountryApiService
import com.udacity.project.app.data.api.CountryResponse
import com.udacity.project.app.data.api.CurrencyInfo
import com.udacity.project.app.data.api.NameInfo

class FakeCountryApiService : CountryApiService {
    var shouldThrow = false
    var responses: List<CountryResponse> = listOf(
        CountryResponse(
            nameInfo = NameInfo(commonName = "Turkmenistan", officialName = "Turkmenistan", nativeNames = emptyList()),
            capitalCities = listOf("Ashgabat"),
            currencyList = listOf(CurrencyInfo(currencyCode = "TMT", currencyName = "Turkmenistan manat", currencySymbol = "m"))
        ),
        CountryResponse(
            nameInfo = NameInfo(commonName = "Japan", officialName = "Japan", nativeNames = emptyList()),
            capitalCities = listOf("Tokyo"),
            currencyList = listOf(CurrencyInfo(currencyCode = "JPY", currencyName = "Japanese yen", currencySymbol = "¥"))
        )
    )

    override suspend fun getCountries(): List<CountryResponse> {
        if (shouldThrow) throw RuntimeException("Network error")
        return responses
    }
}
