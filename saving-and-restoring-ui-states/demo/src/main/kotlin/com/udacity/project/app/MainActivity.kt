package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private  val viewModel: MovieViewModel by viewModels()

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var movieInputEditText: EditText
    private lateinit var addMovieButton: Button
    private lateinit var totalMoviesTextView: TextView
    private lateinit var watchedMoviesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews() {
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView)
        movieInputEditText = findViewById(R.id.movieInputEditText)
        addMovieButton = findViewById(R.id.addMovieButton)
        totalMoviesTextView = findViewById(R.id.totalMoviesTextView)
        watchedMoviesTextView = findViewById(R.id.watchedMoviesTextView)
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

    private fun observeViewModel() {
        viewModel.movies.observe(this) { movies ->
            movieAdapter.updateMovies(movies)
        }

        viewModel.totalMovieCount.observe(this) { count ->
            totalMoviesTextView.text = "Total: $count"
        }

        viewModel.watchedCount.observe(this) { count ->
            watchedMoviesTextView.text = "Watched: $count"
        }
    }
}