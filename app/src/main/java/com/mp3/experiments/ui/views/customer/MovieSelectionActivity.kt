package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp3.experiments.R
import com.mp3.experiments.databinding.ActivityMovieSelectionBinding

class MovieSelectionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMovieSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cinemaLocation = intent.getStringExtra("cinemaLocation")
        val cinemaName = intent.getStringExtra("cinemaName")

        binding.btnSelectMovie.setOnClickListener {
            val intent = Intent(this, SeatSelectionActivity::class.java)
            intent.putExtra("cinemaLocation", cinemaLocation)
            intent.putExtra("cinemaName", cinemaName)
            intent.putExtra("theatreNumber", binding.inputTheatreNumber.text.toString())
            startActivity(intent)
        }
    }
}