package com.udacity.project.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // TODO Step 2.1: Get ViewModel instance using ViewModelProvider
        //  Use: viewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        //  Add import: androidx.lifecycle.ViewModelProvider
        //  DO NOT use: viewModel = MovieViewModel() — that creates a new instance every rotation!
        viewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        updateUI()
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
                updateUI()
            },
            onMovieDeleted = { movieId ->
                viewModel.removeMovie(movieId)
                updateUI()
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
                updateUI()
                movieInputEditText.text.clear()
            }
        }
    }

    private fun updateUI() {
        movieAdapter.updateMovies(viewModel.getMovies())
        totalMoviesTextView.text = "Total: ${viewModel.getTotalMovieCount()}"
        watchedMoviesTextView.text = "Watched: ${viewModel.getWatchedCount()}"
    }
}
