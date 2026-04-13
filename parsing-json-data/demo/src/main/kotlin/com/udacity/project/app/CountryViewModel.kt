package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CountryViewModel : ViewModel() {

    private val countryApiService = CountriesService.api

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    init {
        Log.d("CountryViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun syncCountries() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            try {
                val countryResponses = withContext(Dispatchers.IO) {
                    countryApiService.getCountries()
                }

                _countries.value = countryResponses.map { it.toDomainModel() }
                _syncState.value = SyncState.Success
            } catch (e: JsonDataException) {
                _syncState.value = SyncState.Error("Invalid data format: ${e.message}")
            } catch (e: IOException) {
                _syncState.value = SyncState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Failed to load countries")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CountryViewModel", "ViewModel cleared")
    }
}
