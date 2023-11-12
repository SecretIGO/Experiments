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

        binding.btnSelectCinema.setOnClickListener {
            if (checkIfInput_isEmpty()){
                Toast.makeText(this, "Not valid!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.checkIfCinemaExists(

                binding.inputCinemaLocation.text.toString(),
                binding.inputCinemaName.text.toString())

                .addOnSuccessListener { exists ->
                    if (exists) {
                        val intent = Intent(this, MovieSelectionActivity::class.java)
                        intent.putExtra("cinemaLocation", binding.inputCinemaLocation.text.toString())
                        intent.putExtra("cinemaName", binding.inputCinemaName.text.toString())
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Cinema : ${binding.inputCinemaName.text.toString()}\nLocation : ${binding.inputCinemaLocation.text.toString()}\nDoes Not Exist!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkIfInput_isEmpty() : Boolean{
        if (binding.inputCinemaLocation.text!!.isEmpty()){
            return true
        }
        if (binding.inputCinemaName.text!!.isEmpty()){
            return true
        }
        return false
    }
}