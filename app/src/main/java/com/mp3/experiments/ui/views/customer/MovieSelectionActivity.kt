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
import com.google.android.material.textfield.TextInputEditText
import com.mp3.experiments.R
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
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
    private lateinit var cinemaModel: CinemaModel
    private lateinit var binding: ActivityMovieSelectionBinding
    private lateinit var viewModel: CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        val cinemaLocation = intent.getStringExtra("cinemaLocation")
        val cinemaName = intent.getStringExtra("cinemaName")
        time_inputLogic()

        binding.btnSelectMovie.setOnClickListener {
            if (cinemaLocation != null && cinemaName != null) {

                viewModel.checkIfTimeslotExists(
                    cinemaLocation,
                    cinemaName,
                    binding.inputTheatreNumber.text.toString().toInt(),
                    binding.inputTimeslot.text.toString()
                    ).addOnSuccessListener { exists ->
                        if (exists) {
                            val time = binding.inputTimeslot.text.toString()

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

                                    seatOccupied =
                                        Array(numRows) { BooleanArray(numColumns) { false } }

                                    for (row in 0 until numRows) {
                                        for (col in 0 until numColumns) {
//                                        Log.d("test234", "$row$col")
                                            viewModel.getSeatOccupied(
                                                row,
                                                col,
                                                cinemaLocation,
                                                cinemaName,
                                                "Theatre${binding.inputTheatreNumber.text.toString()}",
                                                time,
                                                lower_length,
                                                middle_length
                                            ) { seatData ->

                                                if (seatData != null) {
                                                    seatOccupied[row][col] = seatData.occupied!!
//                                                Log.d("test123", "${(64 + row + 1).toChar()}${col + 1} : ${seatOccupied[row][col]}")

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
                        } else {
                            Toast.makeText(
                                this,
                                "Timeslot ${binding.inputTimeslot.text.toString()} does not exist!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Log.d("inputError", "No input for Cinema Location or Cinema Name")
            }
        }
    }

    fun time_inputLogic(){
        binding.inputTimeslot.inputType = InputType.TYPE_CLASS_NUMBER
        binding.inputTimeslot.filters = arrayOf(InputFilter.LengthFilter(5))

        binding.inputTimeslot.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 2) {
                    val regex = Regex("^([01]?[0-9]|2[0-3])$")
                    if (!s.matches(regex)){
                        binding.inputTimeslot.setText("")
                    }
                }

                if (s?.length == 5) {
                    val regex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]\$")
                    if (!s.matches(regex)){
                        binding.inputTimeslot.setText("")
                    }
                }

                if (s?.length == 3 && count == 1) {
                    binding.inputTimeslot.setText("${s.substring(0, 2)}:${s.substring(2)}")
                    binding.inputTimeslot.setSelection(4)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    override fun onLoopCompleted() {
        // Start the new activity here
        val intent = Intent(this, SeatSelectionActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("seatOccupied", seatOccupied)
        intent.putExtra("matrixBundle", bundle)
        intent.putExtra("cinemaModel", cinemaModel)
        intent.putExtra("theatreNumber", binding.inputTheatreNumber.text.toString().toInt())
        intent.putExtra("timeslot", binding.inputTimeslot.text.toString())
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