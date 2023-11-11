package com.mp3.experiments.ui.views.admin.a_cinemas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setMargins
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mp3.experiments.R
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.data.viewmodel.MovieViewModel
import com.mp3.experiments.databinding.ActivityAddMovieToTheatreBinding

class AddMovieToTheatreActivity : AppCompatActivity(), LoopCompleteCallbackInterface {

    private val idViewMap = HashMap<String, TextInputEditText>()

    private lateinit var cinemaDetails : CinemaModel
    private lateinit var binding : ActivityAddMovieToTheatreBinding
    private lateinit var movie_viewModel : MovieViewModel
    private lateinit var cinema_viewModel : CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMovieToTheatreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movie_viewModel = MovieViewModel()
        cinema_viewModel = CinemaViewModel()

        binding.btnGenerateTimeslotInputRows.setOnClickListener {
            generateInputRows()
        }

        binding.btnAddMovieToTheatre.setOnClickListener {
            val values = collectInputValues()

            if (checkIfInputs_areValid()){
                cinema_viewModel.checkIfCinemaTheatreExists(
                    binding.inputCinemaLocation.text.toString(),
                    binding.inputCinemaName.text.toString(),
                    binding.inputTheatreNumber.text.toString().toInt()
                    ).addOnSuccessListener{ exists1 ->
                        if (exists1){
                            cinema_viewModel.getCinemaDetails(
                                binding.inputCinemaLocation.text.toString(),
                                binding.inputCinemaName.text.toString()
                                ) { cinemaModel ->
                                    cinemaDetails = cinemaModel!!

                                    movie_viewModel.checkIfMovieExist(
                                        binding.inputMovieName.text.toString()
                                        ).addOnSuccessListener { exists2 ->
                                        if (exists2){
                                            movie_viewModel.addMovie_toTheatre(
                                                binding.inputMovieName.text.toString(),
                                                binding.inputCinemaLocation.text.toString(),
                                                binding.inputCinemaName.text.toString(),
                                                binding.inputTheatreNumber.text.toString().toInt(),
                                                cinemaDetails,
                                                values)
                                        } else {
                                            Toast.makeText(this, "Movie Does not exist!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.d("movie_to_theatre_error", "Failed to get Movie")
                                    }
                                }

                        } else {
                            Toast.makeText(this, "${binding.inputCinemaName.text.toString()} - ${binding.inputCinemaLocation.text.toString()}, Theatre${binding.inputTheatreNumber.text.toString()}\nDoes not Exist!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("movie_to_theatre_error", "Failed to get Cinema")
                    }
            } else {
                Toast.makeText(this, "complete the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateInputRows() {
        binding.container.removeAllViews() // Clear previous input rows

        val numberOfInputRows = binding.inputTimeslotCount.text.toString().toIntOrNull() ?: 0

        for (i in 0 until numberOfInputRows) {
            val rowView = LayoutInflater.from(this).inflate(R.layout.row_timeslot, null)

            val editTextId = "timeslot$i"

            val editText = rowView.findViewById<TextInputEditText>(R.id.input_timeslot_placeholder)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = arrayOf(InputFilter.LengthFilter(5))
            idViewMap[editTextId] = editText

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 2) {
                        val regex = Regex("^([01]?[0-9]|2[0-3])$")
                        if (!s.matches(regex)){
                            editText.setText("")
                        }
                    }

                    if (s?.length == 5) {
                        val regex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]\$")
                        if (!s.matches(regex)){
                            editText.setText("")
                        }
                    }

                    if (s?.length == 3 && count == 1) {
                        editText.setText("${s.substring(0, 2)}:${s.substring(2)}")
                        editText.setSelection(4) // Move cursor after the semicolon
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.container.addView(rowView)
        }
    }

    private fun checkIfInputs_areValid() : Boolean {
        var isValid = true

        if (idViewMap.isEmpty()) {
            isValid = false
            Toast.makeText(this, "Click \"generate\" to create timeslot fields", Toast.LENGTH_SHORT).show()
        }

        if (binding.inputCinemaLocation.text.toString().isBlank()){
            binding.inputCinemaLocation.error = "I think I just saw a ghost! Oh.. it's nothing."
            isValid = false
        }
        if (binding.inputCinemaName.text.toString().isBlank()){
            binding.inputCinemaName.error = "Oh.. Right! I've also been there!"
            isValid = false
        }
        if (binding.inputTheatreNumber.text.toString().isBlank()){
            binding.inputTheatreNumber.error = "Can you see what is wrong? I don't, I see nothing."
        }
        if (binding.inputMovieName.text.toString().isBlank()){
            binding.inputMovieName.error = "That's a great movie, I can tell!"
            isValid = false
        }
        if (binding.inputTimeslotCount.text.toString().isBlank()){
            binding.inputTimeslotCount.error = "So.. when do you expect the customer to watch the movie then?"
            isValid = false
        }

        for ((_, editText) in idViewMap) {
            if (editText.text?.isBlank()!!) {
                editText.error = "I think I see your dad..."
                isValid = false
            } else if (editText.text.toString().length < 5) {
                editText.error = "00:00 - 23:59"
                isValid = false
            } else {
                editText.error = null
            }
        }

        return isValid
    }

    private fun collectInputValues(): List<String> {
        val inputValues = mutableListOf<String>()

        for ((_, editText) in idViewMap) {
            val inputValue = editText.text.toString()
            inputValues.add(inputValue)
        }

        return inputValues
    }

    override fun onLoopCompleted() {
        TODO("Not yet implemented")
    }
}