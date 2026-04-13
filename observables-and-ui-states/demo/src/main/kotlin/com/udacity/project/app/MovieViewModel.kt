package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MovieViewModel : ViewModel() {

    // TODO Step 1.1: Create MutableStateFlow (private) and expose as StateFlow (public)
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private var nextMovieId = 4

    // TODO Step 1.2: Create totalMovieCount computed StateFlow using map() + stateIn()
    val totalMovieCount: StateFlow<Int> = _movies.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // TODO Step 1.2: Create watchedCount computed StateFlow using map() + stateIn()
    val watchedCount: StateFlow<Int> = _movies.map { it.count { movie -> movie.watched } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        // TODO Step 1.1: Set initial value on the MutableStateFlow
        _movies.value = listOf(
            Movie(id = 1, title = "The Dark Knight", watched = false),
            Movie(id = 2, title = "Inception", watched = true),
            Movie(id = 3, title = "Interstellar", watched = false)
        )

        Log.d("MovieViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addMovie(title: String) {
        val newMovie = Movie(id = nextMovieId++, title = title, watched = false)
        _movies.value += newMovie
    }

    fun toggleWatched(movieId: Int) {
        _movies.value = _movies.value.map { movie ->
            if (movie.id == movieId)
                movie.copy(watched = !movie.watched)
            else
                movie
        }
    }

    fun removeMovie(movieId: Int) {
        _movies.value = _movies.value.filter { it.id != movieId }
    }
}