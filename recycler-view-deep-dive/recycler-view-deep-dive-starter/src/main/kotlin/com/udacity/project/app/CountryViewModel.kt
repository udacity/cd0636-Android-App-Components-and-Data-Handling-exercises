package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

/**
 * ViewModel for country management.
 *
 * Complete Part 2 TODOs after implementing Part 1 (CountryAdapter).
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

    /**
     * TODO 2.1: Implement toggleCountryFavorite()
     *
     * Create function that:
     * 1. Gets current countries from countries.value
     * 2. Maps over countries and toggles isFavorite for matching countryId
     * 3. Updates savedStateHandle with modified list
     */
    // fun toggleCountryFavorite(countryId: Int)

    /**
     * TODO 2.2: Implement addCountryWithDetails()
     *
     * Create function that adds a country with all details (for undo functionality):
     * 1. Gets current countries from countries.value
     * 2. Adds the provided country to the list
     * 3. Updates savedStateHandle with modified list
     */
    // fun addCountryWithDetails(country: Country)
}