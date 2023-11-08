package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mp3.experiments.R
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityMovieSelectionBinding

class MovieSelectionActivity : AppCompatActivity(), LoopCompleteCallbackInterface {

    private var upper_length = 0
    private var middle_length = 0
    private var lower_length = 0

    private var upper_width = 0
    private var middle_width = 0
    private var lower_width = 0

    var numRows = upper_length + middle_length + lower_length
    var numColumns = upper_width + middle_width + lower_width

    var seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }
    private lateinit var cinemaModel: CinemaModel // Add this
    private lateinit var binding: ActivityMovieSelectionBinding
    private lateinit var viewModel: CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        val cinemaLocation = intent.getStringExtra("cinemaLocation")
        val cinemaName = intent.getStringExtra("cinemaName")

        binding.btnSelectMovie.setOnClickListener {
            if (cinemaLocation != null && cinemaName != null) {

                viewModel.getCinemaDetails(cinemaLocation, cinemaName) { cinemaModel ->

                    if (cinemaModel != null) {
                        Log.d("test123", "vm reach")

                        upper_length = cinemaModel.cinema_upperbox_length!!
                        middle_length = cinemaModel.cinema_middlebox_length!!
                        lower_length = cinemaModel.cinema_lowerbox_length!!

                        upper_width = cinemaModel.cinema_upperbox_width!!
                        middle_width = cinemaModel.cinema_middlebox_width!!
                        lower_width = cinemaModel.cinema_lowerbox_width!!

                        numRows = upper_length + middle_length + lower_length
                        numColumns = upper_width + middle_width + lower_width

                        seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

                        for (row in 0 until numRows) {
                            for (col in 0 until numColumns) {
                                Log.d("test234", "$row$col")
                                viewModel.getSeatOccupied(
                                    row,
                                    col,
                                    cinemaLocation,
                                    cinemaName,
                                    "Theatre${binding.inputTheatreNumber.text.toString()}",
                                    "",
                                    lower_length,
                                    middle_length
                                ) { seatData ->

                                    if (seatData != null) {
                                        seatOccupied[row][col] = seatData.occupied!!
                                        Log.d("test123", "${(64 + row + 1).toChar()}${col + 1} : ${seatOccupied[row][col]}")

                                        if (row == numRows - 1 && col == numColumns - 1) {
                                            seatOccupied = reverseRows(seatOccupied)
                                            onLoopCompleted()
                                        }
                                    } else {
                                        Log.d("ntest", "seatData is null")
                                    }
                                }
                            }
                        }

                        this.cinemaModel = cinemaModel

                        Log.d("test123", "$upper_length $middle_length $lower_length")
                    } else {
                        Log.d("test123", "vm is null")
                    }
                }
            }
        }
    }
    override fun onLoopCompleted() {
        // Start the new activity here
        val intent = Intent(this, SeatSelectionActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("seatOccupied", seatOccupied)
        intent.putExtra("matrixBundle", bundle)
        intent.putExtra("cinemaModel", cinemaModel)
        intent.putExtra("theatreNumber", binding.inputTheatreNumber.text.toString())
        startActivity(intent)
    }

    fun reverseRows(array: Array<BooleanArray>): Array<BooleanArray> {
        val numRows = array.size
        val reversedArray = Array(numRows) { BooleanArray(array[0].size) }
        for (i in 0 until numRows) {
            reversedArray[i] = array[numRows - i - 1]
        }
        return reversedArray
    }
}