package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp3.experiments.R
import com.mp3.experiments.databinding.ActivityMovieTheatreSelectionBinding

class MovieTheatreSelectionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMovieTheatreSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieTheatreSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectCinema.setOnClickListener {
            val intent = Intent(this, MovieSelectionActivity::class.java)
            intent.putExtra("cinemaLocation", binding.inputCinemaLocation.text.toString())
            intent.putExtra("cinemaName", binding.inputCinemaName.text.toString())
            startActivity(intent)
        }
    }
}