package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.mp3.experiments.R
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.ActivityMovieDetailsBinding
import com.mp3.experiments.databinding.ToolbarLayoutBinding
import com.mp3.experiments.ui.views.customer.user.ProfileActivity

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMovieDetailsBinding
    private lateinit var toolbar : ToolbarLayoutBinding
    private lateinit var auth_vm : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        toolbar = ToolbarLayoutBinding.bind(binding.root)
        setContentView(binding.root)

        toolbar.tvToolbarTitle.setText(R.string.movie_details)
        toolbar.btnBack.setOnClickListener{
            finish()
        }
        toolbar.llUsername.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        auth_vm = UserViewModel()

        auth_vm.getUserProfile()
        auth_vm.getAuthStates().observe(this@MovieDetailsActivity){
            auth_func(it)
        }

        val movie = intent.getParcelableExtra<MovieModel>("movie")

        with(binding){
            Glide.with(this@MovieDetailsActivity)
                .load(movie?.movie_image)
                .fitCenter()
                .into(binding.ivMovieBanner)

            tvMovieName.text = movie?.movie_name
            tvMovieStartDate.text = movie?.movie_date_active
            tvMovieEndDate.text = movie?.movie_date_end
            tvMoviePrice.text = movie?.movie_price.toString()
            tvMovieDescription.text = movie?.movie_description
            tvMovieSynopsis.text = movie?.movie_synopsis

            binding.tvMovieName.isSelected = true

            btnBookMovie.setOnClickListener {
                startActivity(Intent(this@MovieDetailsActivity, MovieTheatreSelectionActivity::class.java))
            }
        }
    }

    private fun auth_func(authenticationStates: AuthenticationStates) {
        when (authenticationStates) {
            is AuthenticationStates.Default -> {
                toolbar.tvUsername.text = authenticationStates.user?.user_details?.username
                Glide.with(this)
                    .load(authenticationStates.user?.user_details?.user_profilePicture)
                    .centerCrop()
                    .into(toolbar.ivUserProfile)
            }
            AuthenticationStates.LogOut -> super.finish()
            else -> {}
        }
    }
}