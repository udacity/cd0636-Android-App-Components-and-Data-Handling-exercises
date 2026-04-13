package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class MovieViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var nextMovieId = 4

    private val defaultMovies = listOf(
        Movie(id = 1, title = "The Shawshank Redemption", watched = false),
        Movie(id = 2, title = "The Dark Knight", watched = true),
        Movie(id = 3, title = "Inception", watched = false)
    )

    val movies: LiveData<List<Movie>> = savedStateHandle.getLiveData("movies", defaultMovies)

    val totalMovieCount: LiveData<Int> = movies.map { it.size }
    val watchedCount: LiveData<Int> = movies.map { it.count { m -> m.watched } }

    init {
        Log.d("MovieViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun addMovie(title: String) {
        val newMovie = Movie(
            id = nextMovieId++,
            title = title,
            watched = false
        )
        val currentMovies = movies.value ?: emptyList()
        savedStateHandle["movies"] = currentMovies + newMovie
    }

    fun toggleWatched(movieId: Int) {
        val currentMovies = movies.value ?: return
        savedStateHandle["movies"] = currentMovies.map { movie ->
            if (movie.id == movieId) movie.copy(watched = !movie.watched) else movie
        }
    }

    fun removeMovie(movieId: Int) {
        val currentMovies = movies.value ?: return
        savedStateHandle["movies"] = currentMovies.filter { it.id != movieId }
    }
}
