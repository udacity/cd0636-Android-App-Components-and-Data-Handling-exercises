package com.udacity.project.app

import com.udacity.project.app.data.db.CountryDao
import com.udacity.project.app.data.db.CountryEntity
import com.udacity.project.app.data.db.CountryWithDetails
import com.udacity.project.app.data.db.CurrencyEntity
import com.udacity.project.app.data.db.NativeNameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCountryDao : CountryDao {
    private val countries = mutableListOf<CountryEntity>()
    private val nativeNames = mutableListOf<NativeNameEntity>()
    private val currencies = mutableListOf<CurrencyEntity>()
    private val countriesFlow = MutableStateFlow<List<CountryWithDetails>>(emptyList())

    override fun getAllCountriesWithDetails(): Flow<List<CountryWithDetails>> = countriesFlow

    override suspend fun insertCountries(countries: List<CountryEntity>) {
        for (country in countries) {
            this.countries.removeAll { it.commonName == country.commonName }
            this.countries.add(country)
        }
        updateFlow()
    }

    override suspend fun insertNativeNames(nativeNames: List<NativeNameEntity>) {
        this.nativeNames.addAll(nativeNames)
        updateFlow()
    }

    override suspend fun insertCurrencies(currencies: List<CurrencyEntity>) {
        this.currencies.addAll(currencies)
        updateFlow()
    }

    override suspend fun deleteAllCountries() {
        countries.clear()
        nativeNames.clear()
        currencies.clear()
        updateFlow()
    }

    private fun updateFlow() {
        countriesFlow.value = countries.map { country ->
            CountryWithDetails(
                country = country,
                nativeNames = nativeNames.filter { it.countryName == country.commonName },
                currencies = currencies.filter { it.countryName == country.commonName }
            )
        }
    }
}
