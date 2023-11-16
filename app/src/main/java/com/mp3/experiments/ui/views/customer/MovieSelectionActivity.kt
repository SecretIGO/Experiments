package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.mp3.experiments.R
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityMovieSelectionBinding
import com.mp3.experiments.databinding.ToolbarLayoutBinding
import com.mp3.experiments.ui.adapters.CinemaAdapter
import com.mp3.experiments.ui.adapters.TheatreMoviesAdapter
import com.mp3.experiments.ui.views.customer.user.ProfileActivity

class MovieSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieSelectionBinding
    private lateinit var toolbar: ToolbarLayoutBinding
    private lateinit var viewModel: CinemaViewModel
    private lateinit var adapter: TheatreMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSelectionBinding.inflate(layoutInflater)
        toolbar = ToolbarLayoutBinding.bind(binding.root)
        setContentView(binding.root)

        toolbar.tvToolbarTitle.setText(R.string.movie_selection)
        toolbar.btnBack.setOnClickListener{
            finish()
        }
        toolbar.llUsername.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        viewModel = CinemaViewModel()

        val cinemaLocation = intent.getStringExtra("cinemaLocation")
        val cinemaName = intent.getStringExtra("cinemaName")

        adapter = TheatreMoviesAdapter(this, ArrayList(), cinemaLocation!!, cinemaName!!,viewModel)
        binding.rvTheatreMovieItem.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvTheatreMovieItem.layoutManager = layoutManager

        viewModel.theatreMoviesList.observe(this, Observer {
            adapter.addMovies(it)
        })
        viewModel.observeTheatreMovies(cinemaLocation, cinemaName)
    }
}