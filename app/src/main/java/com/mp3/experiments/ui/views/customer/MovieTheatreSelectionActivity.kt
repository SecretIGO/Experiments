package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp3.experiments.R
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityMovieTheatreSelectionBinding

class MovieTheatreSelectionActivity : AppCompatActivity() {

    var numRows = 0
    var numColumns = 0

    private lateinit var binding : ActivityMovieTheatreSelectionBinding
    private lateinit var viewModel : CinemaViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieTheatreSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()


    }
    }

