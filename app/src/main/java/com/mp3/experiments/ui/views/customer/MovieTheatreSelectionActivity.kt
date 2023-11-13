package com.mp3.experiments.ui.views.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityMovieTheatreSelectionBinding
import com.mp3.experiments.ui.adapters.CinemaAdapter

class MovieTheatreSelectionActivity : AppCompatActivity() {

    var numRows = 0
    var numColumns = 0

    private lateinit var binding : ActivityMovieTheatreSelectionBinding
    private lateinit var viewModel : CinemaViewModel
    private lateinit var cinemaAdapter : CinemaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieTheatreSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

//        binding.btnSelectCinema.setOnClickListener {
//            if (checkIfInput_isEmpty()){
//                Toast.makeText(this, "Not valid!", Toast.LENGTH_SHORT).show()
//            } else {
//                viewModel.checkIfCinemaExists(
//
//                binding.inputCinemaLocation.text.toString(),
//                binding.inputCinemaName.text.toString())
//
//                .addOnSuccessListener { exists ->
//                    if (exists) {
//                        val intent = Intent(this, MovieSelectionActivity::class.java)
//                        intent.putExtra("cinemaLocation", binding.tvCinemaLocation.text.toString())
//                        intent.putExtra("cinemaName", binding.tvCinemaName.text.toString())
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this, "Cinema : ${binding.inputCinemaName.text.toString()}\nLocation : ${binding.inputCinemaLocation.text.toString()}\nDoes Not Exist!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
    }

//    private fun checkIfInput_isEmpty() : Boolean{
//        if (binding.inputCinemaLocation.text!!.isEmpty()){
//            return true
//        }
//        if (binding.inputCinemaName.text!!.isEmpty()){
//            return true
//        }
//        return false
//    }
}