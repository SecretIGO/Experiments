package com.mp3.experiments.data.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.model.SeatTimeslotModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.nodes.NODE_CINEMA
import com.mp3.experiments.data.nodes.NODE_LOWERBOX
import com.mp3.experiments.data.nodes.NODE_MIDDLEBOX
import com.mp3.experiments.data.nodes.NODE_MOVIES
import com.mp3.experiments.data.nodes.NODE_MOVIE_DETAILS
import com.mp3.experiments.data.nodes.NODE_MOVIE_TIMESLOT
import com.mp3.experiments.data.nodes.NODE_SEATS
import com.mp3.experiments.data.nodes.NODE_UPPERBOX

class MovieViewModel : ViewModel(){

    private val firebase_database = Firebase.database.reference

    fun checkIfMovieExist(movieName : String): Task<Boolean> {
        val movieRef = firebase_database.child(NODE_MOVIES)
            .child(movieName)

        return movieRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                dataSnapshot.exists()
            } else {
                false
            }
        }

    }

    fun createMovie_toDatabase(movie : MovieModel){
        val movieRef = firebase_database.child(NODE_MOVIES)

        movie.movie_name?.let { movieRef.child(it).setValue(movie) }
    }

    fun addMovie_toTheatre(movieName : String, cinemaLocation : String, cinemaName : String, theatreNumber : Int, cinemaDetails : CinemaModel, timeslot_values : List<String>){
        val theatreRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")

        val movieRef = firebase_database.child(NODE_MOVIES)

        val numRow = cinemaDetails.cinema_upperbox_length!! + cinemaDetails.cinema_middlebox_length!! + cinemaDetails.cinema_lowerbox_length!!
        val numCol = cinemaDetails.cinema_upperbox_width!! + cinemaDetails.cinema_middlebox_width!! + cinemaDetails.cinema_lowerbox_width!!

        getMovieDetails(movieRef, movieName) { movie ->
            val theatreMovie = TheatreMovieModel(movie.movie_name, movie.movie_price, movie.movie_date_active, movie.movie_date_end, movie.movie_image)

            theatreRef.child(NODE_MOVIE_DETAILS).setValue(theatreMovie)
        }

        for (row in 0 until numRow) {
            val section =
                if (row < cinemaDetails.cinema_lowerbox_length) NODE_LOWERBOX
                else if (row < cinemaDetails.cinema_lowerbox_length + cinemaDetails.cinema_middlebox_length) NODE_MIDDLEBOX
                else NODE_UPPERBOX

            for (col in 0 until numCol) {
                for ((index, value) in timeslot_values.withIndex()) {
                    val seatTimeslot = SeatTimeslotModel(value, false)

                    theatreRef
                        .child(NODE_SEATS)
                        .child(section)
                        .child((64 + row + 1).toChar().toString())
                        .child("${col + 1}")
                        .child(NODE_MOVIE_TIMESLOT)
                        .child("timeslot${index + 1}")
                        .setValue(seatTimeslot)
                }
            }
        }
    }

    fun getMovieDetails(movieRef: DatabaseReference, movieName: String, callback: (MovieModel) -> Unit) {
        movieRef.child(movieName).get().addOnSuccessListener { snapshot ->
            val movie = snapshot.getValue(MovieModel::class.java)
            if (movie != null) {
                callback.invoke(movie)
            } else {
                Log.d("test123", "movie details not found!")
            }
        }.addOnFailureListener {
            Log.d("test123", "Failure : $it")
        }
    }
}