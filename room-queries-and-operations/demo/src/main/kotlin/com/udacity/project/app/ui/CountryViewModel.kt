package com.udacity.project.app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project.app.data.repo.Country
import com.udacity.project.app.data.repo.CountryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountryViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    val countries: StateFlow<List<Country>> = repository.getCountries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalCount: StateFlow<Int> = countries.mapLatest { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _syncState = MutableStateFlow("")
    val syncState: StateFlow<String> = _syncState.asStateFlow()

    init {
        Log.d("CountryViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _syncState.value = "Loading..."
            repository.refreshCountries().fold(
                onSuccess = { _syncState.value = "Loaded ${countries.value.size} countries" },
                onFailure = { _syncState.value = "Error: ${it.message}" }
            )
        }
    }

    fun syncCountries()  = loadCountries()

}