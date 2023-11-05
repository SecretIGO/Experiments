package com.mp3.experiments.ui.views.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityAddCinemaBinding

class AddCinemaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddCinemaBinding
    private lateinit var viewModel : CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCinemaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        binding.btnAddCinema.setOnClickListener{

            viewModel.checkIfCinemaExists(

                binding.inputCinemaLocation.text.toString(),
                binding.inputCinemaName.text.toString())

            .addOnSuccessListener { exists ->
                Toast.makeText(this, "$exists", Toast.LENGTH_SHORT).show()
                if (exists) {
                    Toast.makeText(this, "${binding.inputCinemaName.text.toString()} at ${binding.inputCinemaLocation.text.toString()} Already Exists!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.createCinema_toDatabase(
                        binding.inputCinemaLocation.text.toString(),
                        binding.inputCinemaName.text.toString(),
                        binding.inputNumOfTheatres.text.toString().toInt(),
                        binding.inputLowerWidth.text.toString().toInt(),
                        binding.inputMiddleWidth.text.toString().toInt(),
                        binding.inputUpperWidth.text.toString().toInt(),
                        binding.inputLowerLength.text.toString().toInt(),
                        binding.inputMiddleLength.text.toString().toInt(),
                        binding.inputUpperLength.text.toString().toInt()
                    )
                }
            }
        }
    }
}