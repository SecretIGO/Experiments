package com.mp3.experiments.ui.adapters

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mp3.experiments.data.model.MovieItemsModel
import com.mp3.experiments.databinding.ListMoviesComingSoonItemsBinding
class ComingSoonAdapter : RecyclerView.Adapter<ComingSoonAdapter.ComingSoonViewHolder>() {

    var comingSoonMovies: List<MovieItemsModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComingSoonViewHolder {
        val binding: ListMoviesComingSoonItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_movi,
            parent,
            false
        )
        return ComingSoonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NowShowingViewHolder, position: Int) {
        holder.bind(comingSoonMovies[position])
    }

    override fun getItemCount(): Int = comingSoonMovies.size

    class NowShowingViewHolder(private val binding: ListMoviesComingSoonItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItemsModel) {
            binding.movie = movie
            // Execute pending bindings right away
            binding.executePendingBindings()
        }
    }
}