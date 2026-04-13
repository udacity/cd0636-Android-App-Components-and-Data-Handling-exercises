package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

/**
 * ViewModel with support for favorite toggling and country management.
 */
class CountryViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val defaultCountries = listOf(
        Country(id = 1, name = "Japan", officialName = "State of Japan", capital = "Tokyo", currencyName = "Japanese yen"),
        Country(id = 2, name = "France", officialName = "French Republic", capital = "Paris", currencyName = "Euro"),
        Country(id = 3, name = "Brazil", officialName = "Federative Republic of Brazil", capital = "Brasília", currencyName = "Brazilian real"),
        Country(id = 4, name = "Kenya", officialName = "Republic of Kenya", capital = "Nairobi", currencyName = "Kenyan shilling"),
        Country(id = 5, name = "Australia", officialName = "Commonwealth of Australia", capital = "Canberra", currencyName = "Australian dollar")
    )

    val countries: LiveData<List<Country>> = savedStateHandle.getLiveData("countries", defaultCountries)

    val totalCount: LiveData<Int> = countries.map { it.size }

    val favoriteCount: LiveData<Int> = countries.map { list ->
        list.count { it.isFavorite }
    }

    init {
        Log.d("CountryViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun deleteCountry(countryId: Int) {
        val currentCountries = countries.value ?: emptyList()
        val updatedCountries = currentCountries.filter { it.id != countryId }
        savedStateHandle["countries"] = updatedCountries
    }

    fun toggleCountryFavorite(countryId: Int) {
        val currentCountries = countries.value ?: emptyList()
        val updatedCountries = currentCountries.map { country ->
            if (country.id == countryId) {
                country.copy(isFavorite = !country.isFavorite)
            } else {
                country
            }
        }
        savedStateHandle["countries"] = updatedCountries
    }

    fun addCountryWithDetails(country: Country) {
        val currentCountries = countries.value ?: emptyList()
        val updatedCountries = currentCountries + country
        savedStateHandle["countries"] = updatedCountries
    }
}
