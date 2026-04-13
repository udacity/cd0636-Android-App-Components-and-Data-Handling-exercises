package com.udacity.project.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovieViewModel : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>(listOf(
        Movie(id = 1, title = "The Shawshank Redemption", watched = false),
        Movie(id = 2, title = "Inception", watched = true),
        Movie(id = 3, title = "Interstellar", watched = false)
    ))
    val movies: LiveData<List<Movie>> = _movies

    private var nextId = 4

    fun addMovie(title: String) {
        val currentMovies = _movies.value ?: emptyList()
        val newMovie = Movie(id = nextId++, title = title, watched = false)
        _movies.value = currentMovies + newMovie
    }

    fun toggleWatched(movieId: Int) {
        val currentMovies = _movies.value ?: emptyList()
        _movies.value = currentMovies.map { movie ->
            if (movie.id == movieId) {
                movie.copy(watched = !movie.watched)
            } else {
                movie
            }
        }
    }

    fun removeMovie(movieId: Int) {
        val currentMovies = _movies.value ?: emptyList()
        _movies.value = currentMovies.filter { it.id != movieId }
    }
}
