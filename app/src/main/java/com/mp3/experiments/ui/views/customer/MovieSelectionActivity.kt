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
            Toast.makeText(this, "item count : ${adapter.itemCount}", Toast.LENGTH_SHORT).show()
        })
        viewModel.observeTheatreMovies(cinemaLocation, cinemaName)

//        time_inputLogic()

//        binding.btnSelectMovie.setOnClickListener {
//            if (cinemaLocation != null && cinemaName != null) {
//
//                viewModel.checkIfTimeslotExists(
//                    cinemaLocation,
//                    cinemaName,
//                    binding.inputTheatreNumber.text.toString().toInt(),
//                    binding.inputTimeslot.text.toString()
//                    ).addOnSuccessListener { exists ->
//                        if (exists) {
//                            val time = binding.inputTimeslot.text.toString()
//
//                            viewModel.getCinemaDetails(cinemaLocation, cinemaName) { cinemaModel ->
//
//                                if (cinemaModel != null) {
//                                    Log.d("test123", "vm reach")
//
//                                    upper_length = cinemaModel.cinema_upperbox_length!!
//                                    middle_length = cinemaModel.cinema_middlebox_length!!
//                                    lower_length = cinemaModel.cinema_lowerbox_length!!
//
//                                    upper_width = cinemaModel.cinema_upperbox_width!!
//                                    middle_width = cinemaModel.cinema_middlebox_width!!
//                                    lower_width = cinemaModel.cinema_lowerbox_width!!
//
//                                    numRows = upper_length + middle_length + lower_length
//                                    numColumns = upper_width + middle_width + lower_width
//
//                                    seatOccupied =
//                                        Array(numRows) { BooleanArray(numColumns) { false } }
//
//                                    for (row in 0 until numRows) {
//                                        for (col in 0 until numColumns) {
////                                        Log.d("test234", "$row$col")
//                                            viewModel.getSeatOccupied(
//                                                row,
//                                                col,
//                                                cinemaLocation,
//                                                cinemaName,
//                                                "Theatre${binding.inputTheatreNumber.text.toString()}",
//                                                time,
//                                                lower_length,
//                                                middle_length
//                                            ) { seatData ->
//
//                                                if (seatData != null) {
//                                                    seatOccupied[row][col] = seatData.occupied!!
////                                                Log.d("test123", "${(64 + row + 1).toChar()}${col + 1} : ${seatOccupied[row][col]}")
//
//                                                    if (row == numRows - 1 && col == numColumns - 1) {
//
//                                                        viewModel.getMovieDetails(cinemaModel.cinema_location!!, cinemaModel.cinema_name!!, binding.inputTheatreNumber.text.toString().toInt()) {movieModel ->
//                                                            theatreMovieModel = movieModel!!
//                                                            seatOccupied = reverseRows(seatOccupied)
//                                                            onLoopCompleted()
//                                                        }
//                                                    }
//                                                } else {
//                                                    Log.d("ntest", "seatData is null")
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    this.cinemaModel = cinemaModel
//
//                                    Log.d("test123", "$upper_length $middle_length $lower_length")
//                                } else {
//                                    Log.d("test123", "vm is null")
//                                }
//                            }
//                        } else {
//                            Toast.makeText(
//                                this,
//                                "Timeslot ${binding.inputTimeslot.text.toString()} does not exist!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//            } else {
//                Log.d("inputError", "No input for Cinema Location or Cinema Name")
//            }
//        }
    }

//    fun time_inputLogic(){
//        binding.inputTimeslot.inputType = InputType.TYPE_CLASS_NUMBER
//        binding.inputTimeslot.filters = arrayOf(InputFilter.LengthFilter(5))
//
//        binding.inputTimeslot.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (s?.length == 2) {
//                    val regex = Regex("^([01]?[0-9]|2[0-3])$")
//                    if (!s.matches(regex)){
//                        binding.inputTimeslot.setText("")
//                    }
//                }
//
//                if (s?.length == 5) {
//                    val regex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]\$")
//                    if (!s.matches(regex)){
//                        binding.inputTimeslot.setText("")
//                    }
//                }
//
//                if (s?.length == 3 && count == 1) {
//                    binding.inputTimeslot.setText("${s.substring(0, 2)}:${s.substring(2)}")
//                    binding.inputTimeslot.setSelection(4)
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })
//    }
}