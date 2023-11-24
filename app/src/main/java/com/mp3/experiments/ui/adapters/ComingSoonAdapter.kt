package com.mp3.experiments.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.databinding.HomeMovieListItemBinding

class ComingSoonAdapter (
    private val context : Context,
    private var movies: MutableList<MovieModel> = mutableListOf()
)  : RecyclerView.Adapter<ComingSoonAdapter.ViewHolder>()  {

    class ViewHolder (
        private val context: Context,
        private val binding: HomeMovieListItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(movies: MovieModel, position: Int){
            Glide.with(context)
                .load(movies.movie_image)
                .fitCenter()
                .into(binding.ivMovieBanner)

            binding.tvMovieName.text = movies.movie_name
            binding.tvMovieName.isSelected = true
        }
    }

    fun addMovies(movie: List<MovieModel>) {
        movies.addAll(movie)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeMovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        movies[position].let {
            holder.bind(it, position)
        }
    }
}