package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class MovieDetailFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()

    private lateinit var movieTitleTextView: TextView
    private lateinit var movieStatusTextView: TextView
    private lateinit var toggleButton: Button
    private lateinit var favoriteButton: Button
    private lateinit var deleteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        val args: MovieDetailFragmentArgs by navArgs()
        val movieId = args.movieId

        setupUI(movieId)
        observeMovie(movieId)
    }

    private fun initializeViews(view: View) {
        movieTitleTextView = view.findViewById(R.id.movieTitleTextView)
        movieStatusTextView = view.findViewById(R.id.movieStatusTextView)
        toggleButton = view.findViewById(R.id.toggleButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        deleteButton = view.findViewById(R.id.deleteButton)
    }

    private fun observeMovie(movieId: Int) {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            val movie = movies.find { it.id == movieId }
            movie?.let { displayMovie(it) }
        }
    }

    private fun displayMovie(movie: Movie) {
        movieTitleTextView.text = movie.title
        movieStatusTextView.text = if (movie.watched) {
            "Status: Watched"
        } else {
            "Status: Not Watched"
        }

        toggleButton.text = if (movie.watched) {
            "Mark as Unwatched"
        } else {
            "Mark as Watched"
        }

        favoriteButton.text = if (movie.isFavorite) {
            "Remove from Favorites"
        } else {
            "Add to Favorites"
        }
    }

    private fun setupUI(movieId: Int) {
        toggleButton.setOnClickListener {
            viewModel.toggleWatched(movieId)
        }

        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite(movieId)
        }

        deleteButton.setOnClickListener {
            viewModel.removeMovie(movieId)
            findNavController().navigateUp()
        }
    }
}
