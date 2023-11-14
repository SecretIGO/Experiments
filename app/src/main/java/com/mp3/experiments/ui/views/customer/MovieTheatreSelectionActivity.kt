package com.mp3.experiments.ui.views.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mp3.experiments.R
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityMovieTheatreSelectionBinding
import com.mp3.experiments.databinding.ToolbarLayoutBinding
import com.mp3.experiments.ui.adapters.CinemaAdapter

class MovieTheatreSelectionActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMovieTheatreSelectionBinding
    private lateinit var viewModel : CinemaViewModel
    private lateinit var cinemaAdapter : CinemaAdapter
    private lateinit var toolbar : ToolbarLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieTheatreSelectionBinding.inflate(layoutInflater)
        toolbar = ToolbarLayoutBinding.bind(binding.root)
        setContentView(binding.root)

        toolbar.tvToolbarTitle.setText(R.string.theatre_selection)
        toolbar.btnBack.setOnClickListener{
            finish()
        }

        viewModel = CinemaViewModel()
        cinemaAdapter = CinemaAdapter(this, ArrayList(), viewModel)
        binding.rvCinemaItems.adapter = cinemaAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvCinemaItems.layoutManager = layoutManager

        viewModel.cinemaList.observe(this, Observer {
            cinemaAdapter.addCinemas(it)
            Toast.makeText(this, "item count : ${cinemaAdapter.itemCount}", Toast.LENGTH_SHORT).show()
        })
        viewModel.observeCinemas()
    }
}