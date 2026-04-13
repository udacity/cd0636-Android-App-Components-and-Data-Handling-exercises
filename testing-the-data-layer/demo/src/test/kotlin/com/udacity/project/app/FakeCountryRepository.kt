package com.udacity.project.app

import com.udacity.project.app.data.repo.Country
import com.udacity.project.app.data.repo.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCountryRepository(
    private val shouldFail: Boolean = false
) : CountryRepository {

    private val countriesFlow = MutableStateFlow<List<Country>>(emptyList())

    private val fakeCountries = listOf(
        Country("Japan", "Japan", "Tokyo", "JPY", "Japanese yen (¥)"),
        Country("Turkmenistan", "Turkmenistan", "Ashgabat", "TMT", "Turkmenistan manat (m)")
    )

    override fun getCountries(): Flow<List<Country>> = countriesFlow

    override suspend fun refreshCountries(): Result<Unit> {
        return if (shouldFail) {
            Result.failure(RuntimeException("Network error"))
        } else {
            countriesFlow.value = fakeCountries
            Result.success(Unit)
        }
    }
}
