package com.mp3.experiments.ui.adapters

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.TheatremovieListItemBinding
import com.mp3.experiments.ui.views.customer.SeatSelectionActivity

class TheatreMoviesAdapter (
    private val context : Context,
    private var theatreMoviesList: MutableList<TheatreModel> = mutableListOf(),
    private var cinemaLocation: String,
    private var cinemaName : String,
    private var viewModel : CinemaViewModel
)  : RecyclerView.Adapter<TheatreMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TheatremovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding, cinemaLocation, cinemaName, viewModel)
    }

    override fun getItemCount(): Int {
        return theatreMoviesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("test123", "Iteration : ${position+1}")

        theatreMoviesList[position].let {
            holder.bind(it, position)
        }
    }

    fun addMovies(movies: List<TheatreModel>) {
        theatreMoviesList.addAll(movies)
        notifyDataSetChanged()
    }

    class ViewHolder(

        private val context: Context,
        private val binding: TheatremovieListItemBinding,
        private var cinemaLocation: String,
        private var cinemaName : String,
        private val viewModel: CinemaViewModel

    ) : RecyclerView.ViewHolder(binding.root){

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

        var selectedTimeslot = ""
        private lateinit var theatreMovieModel : TheatreMovieModel

        fun bind(movie: TheatreModel, position: Int){
            if(movie.movieDetails?.movie_name.isNullOrEmpty()){
                binding.itemMovieItem.visibility = View.GONE
            }

            binding.tvMovieName.text = movie.movieDetails?.movie_name
            binding.tvMoviePrice.text = movie.movieDetails?.movie_price.toString()
            binding.tvTheatreNumber.text = "Theatre${position+1}"
            binding.tvMovieName.isSelected = true
            Glide.with(context)
                .load(movie.movieDetails?.movie_image)
                .fitCenter()
                .into(binding.ivMovieBanner)

            viewModel.getMovieTimeslots(cinemaLocation, cinemaName, position+1) { timeslots ->

                if (timeslots.isEmpty()) {
                    Log.d("test1234", "Timeslot is Empty")
                } else {
                    val timeslotStrings = mutableListOf("Choose...")
                    timeslotStrings.addAll(timeslots.map { it?.time ?: "" })

                    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, timeslotStrings)
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    binding.spinner.adapter = adapter
                }
            }

//            selectedTimeslot = binding.spinner.selectedItem.toString()
//            Log.d("SelectedTimeslot", "initial : " + selectedTimeslot)

            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedTimeslot = binding.spinner.selectedItem.toString()
                    Log.d("SelectedTimeslot", selectedTimeslot)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected if needed
                }
            }

            binding.itemMovieItem.setOnClickListener {
                if (selectedTimeslot.isEmpty() || selectedTimeslot == "Choose..."){
                    Toast.makeText(context, "Select a timeslot :)", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.checkIfTimeslotExists(
                        cinemaLocation,
                        cinemaName,
                        position+1,
                        selectedTimeslot
                    ).addOnSuccessListener { exists ->
                        if (exists) {
                            val time = selectedTimeslot

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
                                            viewModel.getSeatOccupied(
                                                row,
                                                col,
                                                cinemaLocation,
                                                cinemaName,
                                                "Theatre${position+1}",
                                                time,
                                                lower_length,
                                                middle_length
                                            ) { seatData ->
                                                if (seatData != null) {
                                                    seatOccupied[row][col] = seatData.occupied!!
                                                    if (row == numRows - 1 && col == numColumns - 1) {

                                                        viewModel.getMovieDetails(cinemaModel.cinema_location!!, cinemaModel.cinema_name!!, position+1) {movieModel ->
                                                            theatreMovieModel = movieModel!!
                                                            seatOccupied = reverseRows(seatOccupied)
                                                            onLoopCompleted()
                                                        }
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
                            Log.d("test123", "Timeslot Does not Exist!")
                        }
                    }
                }
            }
        }

        private fun onLoopCompleted() {
            // Start the new activity here
            val intent = Intent(context, SeatSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("seatOccupied", seatOccupied)
            intent.putExtra("matrixBundle", bundle)
            intent.putExtra("cinemaModel", cinemaModel)
            intent.putExtra("theatreMovieModel", theatreMovieModel)
            intent.putExtra("theatreNumber", position+1)
            intent.putExtra("timeslot", selectedTimeslot)
            context.startActivity(intent)
        }

        private fun reverseRows(array: Array<BooleanArray>): Array<BooleanArray> {
            val numRows = array.size
            val reversedArray = Array(numRows) { BooleanArray(array[0].size) }
            for (i in 0 until numRows) {
                reversedArray[i] = array[numRows - i - 1]
            }
            return reversedArray
        }
    }


}