package com.udacity.project.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MovieViewModel

    // Movie UI
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var movieInputEditText: EditText
    private lateinit var addMovieButton: Button
    private lateinit var totalMoviesTextView: TextView
    private lateinit var watchedMoviesTextView: TextView

    // Sync UI
    private lateinit var syncButton: Button
    private lateinit var syncParallelButton: Button
    private lateinit var syncWithProgressButton: Button
    private lateinit var cancelSyncButton: Button
    private lateinit var syncProgressBar: ProgressBar
    private lateinit var syncStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        observeSyncState()
        observeSyncProgress()
        updateUI()
    }

    private fun initializeViews() {
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView)
        movieInputEditText = findViewById(R.id.movieInputEditText)
        addMovieButton = findViewById(R.id.addMovieButton)
        totalMoviesTextView = findViewById(R.id.totalMoviesTextView)
        watchedMoviesTextView = findViewById(R.id.watchedMoviesTextView)

        syncButton = findViewById(R.id.syncButton)
        syncParallelButton = findViewById(R.id.syncParallelButton)
        syncWithProgressButton = findViewById(R.id.syncWithProgressButton)
        cancelSyncButton = findViewById(R.id.cancelSyncButton)
        syncProgressBar = findViewById(R.id.syncProgressBar)
        syncStatusTextView = findViewById(R.id.syncStatusTextView)
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

        syncButton.setOnClickListener {
            viewModel.syncMovies()
        }

        syncParallelButton.setOnClickListener {
            viewModel.syncMoviesParallel()
        }

        syncWithProgressButton.setOnClickListener {
            viewModel.syncMoviesWithProgress()
        }

        cancelSyncButton.setOnClickListener {
            viewModel.cancelSync()
        }
    }

    private fun observeSyncState() {
        lifecycleScope.launch {
            viewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Idle -> {
                        syncStatusTextView.visibility = View.GONE
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                    }
                    is SyncState.Loading -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Syncing movies..."
                        enableSyncButtons(false)
                        cancelSyncButton.isEnabled = true
                    }
                    is SyncState.Success -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Sync completed!"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                        updateUI()

                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 2000)
                    }
                    is SyncState.Error -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Error: ${state.message}"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)
                    }
                    is SyncState.Cancelled -> {
                        syncStatusTextView.visibility = View.VISIBLE
                        syncStatusTextView.text = "Sync cancelled"
                        syncProgressBar.visibility = View.GONE
                        enableSyncButtons(true)

                        syncStatusTextView.postDelayed({
                            syncStatusTextView.visibility = View.GONE
                        }, 2000)
                    }
                }
            }
        }
    }

    private fun observeSyncProgress() {
        lifecycleScope.launch {
            viewModel.syncProgress.collect { progress ->
                if (progress > 0) {
                    syncProgressBar.visibility = View.VISIBLE
                    syncProgressBar.progress = progress
                } else {
                    syncProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun enableSyncButtons(enabled: Boolean) {
        syncButton.isEnabled = enabled
        syncParallelButton.isEnabled = enabled
        syncWithProgressButton.isEnabled = enabled
        cancelSyncButton.isEnabled = !enabled
    }

    private fun updateUI() {
        movieAdapter.updateMovies(viewModel.getMovies())
        totalMoviesTextView.text = "Total: ${viewModel.getTotalMovieCount()}"
        watchedMoviesTextView.text = "Watched: ${viewModel.getWatchedCount()}"
    }
}
