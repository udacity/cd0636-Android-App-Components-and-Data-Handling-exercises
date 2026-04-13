package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MovieViewModel

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var movieInputEditText: EditText
    private lateinit var addMovieButton: Button
    private lateinit var totalMoviesTextView: TextView
    private lateinit var watchedMoviesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        // TODO Step 2.1: Call observeViewModelWithFlow()
        observeViewModelWithFlow()
    }

    private fun initializeViews() {
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView)
        movieInputEditText = findViewById(R.id.movieInputEditText)
        addMovieButton = findViewById(R.id.addMovieButton)
        totalMoviesTextView = findViewById(R.id.totalMoviesTextView)
        watchedMoviesTextView = findViewById(R.id.watchedMoviesTextView)
    }

    // TODO Step 2.1: Collect StateFlows inside repeatOnLifecycle — each collect() needs its own launch
    private fun observeViewModelWithFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.movies.collect { movies ->
                        movieAdapter.updateMovies(movies)
                    }
                }

                launch {
                    viewModel.totalMovieCount.collect { count ->
                        totalMoviesTextView.text = "Total: $count"
                    }
                }

                launch {
                    viewModel.watchedCount.collect { count ->
                        watchedMoviesTextView.text = "Watched: $count"
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieToggled = { movieId ->
                viewModel.toggleWatched(movieId)
            },
            onMovieDeleted = { movieId ->
                viewModel.removeMovie(movieId)
            }
        )
        moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }
    }

    private fun setupClickListeners() {
        addMovieButton.setOnClickListener {
            val title = movieInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addMovie(title)
                movieInputEditText.text.clear()
            }
        }
    }
}