package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel

// TODO Step 1.1: Extend ViewModel so this class survives configuration changes
//  Change the class declaration to: class MovieViewModel : ViewModel()
//  Add import: androidx.lifecycle.ViewModel
class MovieViewModel : ViewModel()  {

    private val _movies = mutableListOf<Movie>()
    private var nextMovieId = 4

    init {
        _movies.add(Movie(id = 1, title = "The Shawshank Redemption", watched = false))

        Log.d("MovieViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun getMovies(): List<Movie> {
        return _movies.toList()
    }

    fun addMovie(title: String) {
        val newMovie = Movie(
            id = nextMovieId++,
            title = title,
            watched = false
        )
        _movies.add(newMovie)
    }

    fun toggleWatched(movieId: Int) {
        val index = _movies.indexOfFirst { it.id == movieId }
        if (index != -1) {
            val movie = _movies[index]
            _movies[index] = movie.copy(watched = !movie.watched)
        }
    }

    fun removeMovie(movieId: Int) {
        _movies.removeIf { it.id == movieId }
    }

    fun getTotalMovieCount(): Int {
        return _movies.size
    }

    fun getWatchedCount(): Int {
        return _movies.count { it.watched }
    }
}
