package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val onMovieClicked: (Movie) -> Unit,
    private val onMovieToggled: (Int) -> Unit,
    private val onMovieDeleted: (Int) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = emptyList()

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val movieCheckbox: CheckBox = itemView.findViewById(R.id.movieCheckbox)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(movie: Movie) {
            movieTitle.text = movie.title
            movieCheckbox.isChecked = movie.watched

            itemView.setOnClickListener {
                onMovieClicked(movie)
            }

            movieCheckbox.setOnClickListener {
                onMovieToggled(movie.id)
            }

            deleteButton.setOnClickListener {
                onMovieDeleted(movie.id)
            }
        }
    }
}
