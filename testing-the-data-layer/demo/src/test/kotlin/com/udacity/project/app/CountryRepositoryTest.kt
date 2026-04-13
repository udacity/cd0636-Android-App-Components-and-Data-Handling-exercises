package com.udacity.project.app

import com.google.common.truth.Truth.assertThat
import com.udacity.project.app.data.api.CountryResponse
import com.udacity.project.app.data.api.CurrencyInfo
import com.udacity.project.app.data.api.NameInfo
import com.udacity.project.app.data.repo.DefaultCountryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryRepositoryTest {

    private lateinit var fakeDao: FakeCountryDao
    private lateinit var fakeApiService: FakeCountryApiService
    private lateinit var repository: DefaultCountryRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        fakeDao = FakeCountryDao()
        fakeApiService = FakeCountryApiService()
        repository = DefaultCountryRepository(
            countryDao = fakeDao,
            countryApiService = fakeApiService,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun getCountries_emptyByDefault() = runTest(testDispatcher) {
        val countries = repository.getCountries().first()
        assertThat(countries).isEmpty()
    }

    @Test
    fun refreshCountries_networkError_returnsFailure() = runTest(testDispatcher) {
        fakeApiService.shouldThrow = true

        val result = repository.refreshCountries()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun refreshCountries_replacesOldData() = runTest(testDispatcher) {
        repository.refreshCountries()

        fakeApiService.responses = listOf(
            CountryResponse(
                nameInfo = NameInfo(
                    commonName = "Brazil",
                    officialName = "Federative Republic of Brazil",
                    nativeNames = emptyList()
                ),
                capitalCities = listOf("Brasília"),
                currencyList = listOf(
                    CurrencyInfo(
                        currencyCode = "BRL",
                        currencyName = "Brazilian real",
                        currencySymbol = "R$"
                    )
                )
            )
        )
        repository.refreshCountries()

        val countries = repository.getCountries().first()
        assertThat(countries).hasSize(1)
        assertThat(countries[0].name).isEqualTo("Brazil")
    }

    @Test
    fun refreshCountries_mapsCurrencyCorrectly() = runTest(testDispatcher) {
        repository.refreshCountries()
        val countries = repository.getCountries().first()
        val japan = countries.find { it.name == "Japan" }!!
        assertThat(japan.capital).isEqualTo("Tokyo")
        assertThat(japan.currencyCode).isEqualTo("JPY")
    }
}
