package com.udacity.project.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.project.app.databinding.FragmentMovieListBinding

class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
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

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addMovieButton.setOnClickListener {
            val title = binding.movieInputEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addMovie(title)
                binding.movieInputEditText.text?.clear()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.updateMovies(movies)
            binding.totalMoviesTextView.text = "Total: ${movies.size}"
            binding.watchedMoviesTextView.text = "Watched: ${movies.count { it.watched }}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
