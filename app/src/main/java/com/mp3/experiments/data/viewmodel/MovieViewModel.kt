package com.mp3.experiments.data.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
import com.mp3.experiments.data.states.StorageStates
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class MovieViewModel : ViewModel(){

    private val firebase_database = Firebase.database.reference
    private val firebase_storage = Firebase.storage.reference

    private var storage_states = MutableLiveData<StorageStates>()

    private val _moviesNowShowing = MutableLiveData<List<MovieModel>>()
    val moviesNowShowing: LiveData<List<MovieModel>> get() = _moviesNowShowing

    private val _moviesComingSoon = MutableLiveData<List<MovieModel>>()
    val moviesComingSoon: LiveData<List<MovieModel>> get() = _moviesComingSoon

    fun checkIfMovieExist(movieName : String): Task<Boolean> {
        val movieRef = firebase_database
            .child(NODE_MOVIES)
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

    fun getNowShowingMovies(){

    }

    fun createMovie_toDatabase(movie : MovieModel, img: ByteArray){
        val movieRef = firebase_database
            .child(NODE_MOVIES)
        val cinemaLogoReference = firebase_storage
            .child(NODE_MOVIES)
            .child("${movie.movie_name}.jpg")

        cinemaLogoReference.putBytes(img).addOnSuccessListener {
            cinemaLogoReference.downloadUrl.addOnSuccessListener {
                movie.movie_image = it.toString()
                movieRef.child(movie.movie_name!!).setValue(movie)
            }.addOnFailureListener {
                storage_states.value = StorageStates.StorageFailed(it.message)
            }

        }.addOnFailureListener {
            storage_states.value = StorageStates.StorageFailed(it.message)
        }

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

    fun observeNowShowingMovies() {
        val movieRef = firebase_database.child(NODE_MOVIES)

        val moviesListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val moviesList: MutableList<MovieModel> = mutableListOf()

                for (movieSnapshot in dataSnapshot.children) {
                    val movie = movieSnapshot.getValue<MovieModel>()

                    val status = isMovieWithinCurrentDate(movie?.movie_date_active, movie?.movie_date_end)

                    Log.d("MovieDateChecker", "${movie?.movie_name} - Now Showing? : $status")

                    if(status)
                        moviesList.add(movie!!)
                }

                _moviesNowShowing.value = moviesList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if necessary
                Log.e("CinemaObserver", "Error loading cinemas: ${databaseError.message}")
            }
        }

        movieRef.addValueEventListener(moviesListener)
    }

    fun observeComingSoonMovies() {
        val movieRef = firebase_database.child(NODE_MOVIES)

        val moviesListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val moviesList: MutableList<MovieModel> = mutableListOf()

                for (movieSnapshot in dataSnapshot.children) {
                    val movie = movieSnapshot.getValue<MovieModel>()

                    val status = isMovieComingSoon(movie?.movie_date_active!!)

                    Log.d("MovieDateChecker", "${movie.movie_name} - Coming Soon? : $status")

                    if(status)
                        moviesList.add(movie)
                }

                _moviesComingSoon.value = moviesList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CinemaObserver", "Error loading cinemas: ${databaseError.message}")
            }
        }

        movieRef.addValueEventListener(moviesListener)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun isMovieWithinCurrentDate(startDate: String?, endDate: String?): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDate = Calendar.getInstance().time

        try {
            val movieStartDate = dateFormat.parse(startDate!!)
            val movieEndDate = dateFormat.parse(endDate!!)

            return currentDate.after(movieStartDate) && currentDate.before(movieEndDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun isMovieComingSoon(startDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDate = Calendar.getInstance().time

        try {
            val movieStartDate = dateFormat.parse(startDate)

            return currentDate.before(movieStartDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}