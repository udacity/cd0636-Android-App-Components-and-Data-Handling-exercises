package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WatchedListFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var addMovieFab: FloatingActionButton
    private lateinit var totalMoviesTextView: TextView
    private lateinit var watchedMoviesTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        moviesRecyclerView = view.findViewById(R.id.moviesRecyclerView)
        addMovieFab = view.findViewById(R.id.addMovieFab)
        totalMoviesTextView = view.findViewById(R.id.totalMoviesTextView)
        watchedMoviesTextView = view.findViewById(R.id.watchedMoviesTextView)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(
            onMovieClicked = { movie ->
                val action = WatchedListFragmentDirections.actionWatchedToDetail(movie.id)
                findNavController().navigate(action)
            },
            onMovieToggled = { movieId ->
                viewModel.toggleWatched(movieId)
            },
            onMovieDeleted = { movieId ->
                viewModel.removeMovie(movieId)
            }
        )
        moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupClickListeners() {
        addMovieFab.setOnClickListener {
            val action = WatchedListFragmentDirections.actionWatchedToAddMovie()
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            val watched = movies.filter { it.watched }
            movieAdapter.updateMovies(watched)
        }

        viewModel.totalMovieCount.observe(viewLifecycleOwner) { count ->
            totalMoviesTextView.text = "Total: $count"
        }

        viewModel.watchedCount.observe(viewLifecycleOwner) { count ->
            watchedMoviesTextView.text = "Watched: $count"
        }
    }
}
