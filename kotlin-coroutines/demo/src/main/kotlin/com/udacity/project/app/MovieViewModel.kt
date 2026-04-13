package com.udacity.project.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class MovieViewModel : ViewModel() {

    private val _movies = mutableListOf<Movie>()
    private var nextMovieId = 4

    // Sync state management
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Progress tracking
    private val _syncProgress = MutableStateFlow(0)
    val syncProgress: StateFlow<Int> = _syncProgress.asStateFlow()

    // Job reference for cancellation
    private var syncJob: Job? = null

    init {
        _movies.add(Movie(id = 1, title = "The Shawshank Redemption", watched = false))
        _movies.add(Movie(id = 2, title = "Inception", watched = true))
        _movies.add(Movie(id = 3, title = "Interstellar", watched = false))

        Log.d("MovieViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun getMovies(): List<Movie> = _movies.toList()

    fun addMovie(title: String) {
        val newMovie = Movie(id = nextMovieId++, title = title, watched = false)
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

    fun getTotalMovieCount(): Int = _movies.size
    fun getWatchedCount(): Int = _movies.count { it.watched }

    fun syncMovies() {
        viewModelScope.launch {
            try {
                _syncState.value = SyncState.Loading

                val movies = withContext(Dispatchers.IO) {
                    fetchMoviesFromServer()
                }

                _movies.clear()
                _movies.addAll(movies)
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            }
        }
    }

    fun syncMoviesParallel() {
        viewModelScope.launch {
            try {
                _syncState.value = SyncState.Loading

                val moviesDeferred = async(Dispatchers.IO) {
                    fetchMoviesFromServer()
                }
                val watchedCountDeferred = async(Dispatchers.IO) {
                    fetchWatchedCountFromServer()
                }

                val movies = moviesDeferred.await()
                val watchedCount = watchedCountDeferred.await()

                Log.d("MovieViewModel", "Server watched count: $watchedCount")

                _movies.clear()
                _movies.addAll(movies)
                _syncState.value = SyncState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Parallel sync failed")
            }
        }
    }

    fun syncMoviesWithProgress() {
        syncJob = viewModelScope.launch {
            try {
                _syncState.value = SyncState.Loading
                _syncProgress.value = 0

                withContext(Dispatchers.IO) {
                    _syncProgress.value = 25
                    val movies = fetchMoviesFromServer()

                    ensureActive()

                    _syncProgress.value = 50
                    processMovies(movies)

                    ensureActive()

                    _syncProgress.value = 75
                    _movies.clear()
                    _movies.addAll(movies)

                    ensureActive()

                    _syncProgress.value = 100
                }

                _syncState.value = SyncState.Success
            }catch (e: CancellationException){
                _syncState.value = SyncState.Cancelled
                _syncProgress.value = 0
                throw e
            }catch (e: Exception){
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
                _syncProgress.value = 0
            }
        }
    }

    fun cancelSync() {
        syncJob?.cancel()
        _syncState.value = SyncState.Cancelled
        _syncProgress.value = 0
    }

    private suspend fun fetchMoviesFromServer(): List<Movie> {
        delay(1000)
        return listOf(
            Movie(1, "The Dark Knight", true),
            Movie(2, "Pulp Fiction", false),
            Movie(3, "The Godfather", true),
            Movie(4, "Fight Club", false),
            Movie(5, "Forrest Gump", true)
        )
    }

    private suspend fun fetchWatchedCountFromServer(): Int {
        delay(800)
        return 3
    }

    private suspend fun processMovies(movies: List<Movie>) {
        delay(500)
    }

    override fun onCleared() {
        super.onCleared()
        cancelSync()
        Log.d("MovieViewModel", "ViewModel cleared - cancelling operations")
    }
}
