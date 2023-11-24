package com.mp3.experiments.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp3.experiments.data.model.HomeMovieModel
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.databinding.CarouselItemBinding
import com.mp3.experiments.ui.views.customer.MovieTheatreSelectionActivity

class MovieCarouselAdapter(private val movieList: List<HomeMovieModel>): RecyclerView.Adapter<MovieCarouselAdapter.MovieViewHolder>() {
    class MovieViewHolder(val binding: CarouselItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)

    }
    override fun onBindViewHolder(holder: MovieCarouselAdapter.MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.binding.apply {
            Glide.with(movieImage).load(movie.image).into(movieImage)
            movieName.text = movie.name

            movieImage.setOnClickListener {

                val intent = Intent(it.context, MovieTheatreSelectionActivity::class.java)

                // You can also pass data to the new activity if needed
                intent.putExtra("movieName", movie.name)

                it.context.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int = movieList.size
    }


