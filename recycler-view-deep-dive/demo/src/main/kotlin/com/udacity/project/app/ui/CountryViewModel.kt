package com.udacity.project.app.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.project.app.data.repo.Country
import com.udacity.project.app.data.repo.CountryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountryViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())

    val countries: StateFlow<List<Country>> = repository.getCountries()
        .combine(_favorites) { countries, favorites ->
            countries.map { country ->
                country.copy(isFavorite = country.countryName in favorites)
            }
        }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val favoriteCount: StateFlow<Int> = countries.mapLatest { list ->
        list.count { it.isFavorite }
    }.stateIn(
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

    fun syncCountries() = loadCountries()

    fun toggleFavorite(countryName: String) {
        _favorites.value = if (countryName in _favorites.value) {
            _favorites.value - countryName
        } else {
            _favorites.value + countryName
        }
    }
}

class CountryViewModelFactory(
    private val repository: CountryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CountryViewModel(repository) as T
    }
}