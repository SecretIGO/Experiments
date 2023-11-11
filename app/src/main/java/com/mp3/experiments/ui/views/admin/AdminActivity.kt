package com.mp3.experiments.ui.views.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp3.experiments.databinding.ActivityAdminBinding
import com.mp3.experiments.ui.views.admin.a_cinemas.AddCinemaActivity
import com.mp3.experiments.ui.views.admin.a_cinemas.AddMovieToTheatreActivity
import com.mp3.experiments.ui.views.admin.a_movies.AddMovieActivity

class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate((layoutInflater))
        setContentView(binding.root)

        binding.btnAddCinema.setOnClickListener {
            startActivity(Intent(this, AddCinemaActivity::class.java))
        }

        binding.btnEditTheatre.setOnClickListener {
            startActivity(Intent(this, AddMovieToTheatreActivity::class.java))
        }

        binding.btnAddMovie.setOnClickListener {
            startActivity(Intent(this, AddMovieActivity::class.java))
        }
    }
}