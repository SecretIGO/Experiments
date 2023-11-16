package com.mp3.experiments.ui.adapters

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mp3.experiments.data.model.MovieItemsModel
import com.mp3.experiments.databinding.ListMoviesNowShowingItemsBinding


class NowShowingAdapter (

    private val context : Context,

    private var viewModel : MovieItemsModel,

)

    var nowShowingMovies: List<MovieItemsModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowShowingViewHolder {
        val binding: ListMoviesNowShowingItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_movies_now_showing_items,
            parent,
            false
        )
        return NowShowingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NowShowingViewHolder, position: Int) {
        holder.bind(nowShowingMovies[position])
    }

    override fun getItemCount(): Int = nowShowingMovies.size

    class NowShowingViewHolder(private val binding: ListMoviesNowShowingItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItemsModel) {
            binding.movie = movie
            // Execute pending bindings right away
            binding.executePendingBindings()
        }
    }
}












