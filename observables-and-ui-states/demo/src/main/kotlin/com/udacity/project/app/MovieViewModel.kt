package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class MovieViewModel : ViewModel() {

    // TODO Step 1.1: Create MutableLiveData (private) and expose as LiveData (public)
    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private var nextMovieId = 4

    // TODO Step 1.2: Create totalMovieCount computed LiveData using map()
    val totalMovieCount: LiveData<Int> = _movies.map { it.size }

    // TODO Step 1.2: Create watchedCount computed LiveData using map()
    val watchedCount: LiveData<Int> = _movies.map { it.count { movie -> movie.watched } }

    init {
        // TODO Step 1.1: Set initial value on the MutableLiveData
        _movies.value = listOf(
            Movie(id = 1, title = "The Dark Knight", watched = false),
            Movie(id = 2, title = "Inception", watched = true),
            Movie(id = 3, title = "Interstellar", watched = false)
        )

        Log.d("MovieViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addMovie(title: String) {
        val newMovie = Movie(id = nextMovieId++, title = title, watched = false)
        _movies.value = (_movies.value ?: emptyList()) + newMovie
    }

    fun toggleWatched(movieId: Int) {
        _movies.value = (_movies.value ?: emptyList()).map { movie ->
            if (movie.id == movieId)
                movie.copy(watched = !movie.watched)
            else
                movie
        }
    }

    fun removeMovie(movieId: Int) {
        _movies.value = (_movies.value ?: emptyList()).filter { it.id != movieId }
    }
}
