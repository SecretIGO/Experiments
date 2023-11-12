package com.mp3.experiments.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.SeatTimeslotModel
import com.mp3.experiments.data.model.SeatsModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.nodes.NODE_CINEMA
import com.mp3.experiments.data.nodes.NODE_CINEMA_DETAILS
import com.mp3.experiments.data.nodes.NODE_LOWERBOX
import com.mp3.experiments.data.nodes.NODE_MIDDLEBOX
import com.mp3.experiments.data.nodes.NODE_MOVIE_DETAILS
import com.mp3.experiments.data.nodes.NODE_MOVIE_TIMESLOT
import com.mp3.experiments.data.nodes.NODE_SEATS
import com.mp3.experiments.data.nodes.NODE_UPPERBOX

class CinemaViewModel : ViewModel() {
    private val firebase_database = Firebase.database.reference

    fun getSeatOccupied(
        row: Int,
        col: Int,
        cinemaLocation: String,
        cinemaName: String,
        theaterName: String,
        showtime : String,
        lowerbox_length: Int,
        middlebox_length: Int,
        callback: (SeatTimeslotModel?) -> Unit
    ) {
        val section =
            if (row < lowerbox_length) NODE_LOWERBOX
            else if (row < lowerbox_length + middlebox_length) NODE_MIDDLEBOX
            else NODE_UPPERBOX

        val seatOccupancyRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child(theaterName)
            .child(NODE_SEATS)
            .child(section)
            .child("${(64+row+1).toChar()}")
            .child("${col + 1}")
            .child(NODE_MOVIE_TIMESLOT)
            .orderByChild("time")
            .equalTo(showtime)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val time = childSnapshot.child("time").getValue(String::class.java)
                    val occupied = childSnapshot.child("occupied").getValue(Boolean::class.java)

                    val seatOccupied = SeatTimeslotModel(time, occupied)
                    callback(seatOccupied)
//                    Log.d( "test123","Time: $time, Occupied: $occupied")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
                Log.d("test123", "fun cancelled: $databaseError")
            }
        }
        seatOccupancyRef.addValueEventListener(valueEventListener)
    }

    fun checkIfSeat_isOccupied(
        seatSelected : Array<BooleanArray>,
        cinemaLocation: String,
        cinemaName: String,
        theatreNumber: Int,
        lowerbox_length: Int,
        middlebox_length: Int,
        row: Int,
        col: Int,
        showtime: String
        ) : Task<Boolean> {

        val section =
            if (row < lowerbox_length) NODE_LOWERBOX
            else if (row < lowerbox_length + middlebox_length) NODE_MIDDLEBOX
            else NODE_UPPERBOX

        val timeslotRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")
            .child(NODE_SEATS)
            .child(section)
            .child("${(64 + row + 1).toChar()}")
            .child("${col + 1}")
            .child(NODE_MOVIE_TIMESLOT)
            .orderByChild("time")
            .equalTo(showtime)

        return timeslotRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result

                for (timeslotSnapshot in dataSnapshot.children) {
                    val timeslot = timeslotSnapshot.getValue(SeatTimeslotModel::class.java)
                    if (timeslot?.occupied == true && seatSelected[row][col]){
                        return@continueWith true
                    }
                }
                false
            } else {
                Log.e("test12", "Error getting timeslot", task.exception)
                false
            }
        }
    }

    fun updateSeatOccupied(
        cinemaLocation: String,
        cinemaName: String,
        theatreNumber: Int,
        lowerbox_length: Int,
        middlebox_length: Int,
        row: Int,
        col: Int,
        showtime: String
    ) {
        val section =
            if (row < lowerbox_length) NODE_LOWERBOX
            else if (row < lowerbox_length + middlebox_length) NODE_MIDDLEBOX
            else NODE_UPPERBOX

        val timeslotRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")
            .child(NODE_SEATS)
            .child(section)
            .child("${(64 + row + 1).toChar()}")
            .child("${col + 1}")
            .child(NODE_MOVIE_TIMESLOT)
            .orderByChild("time")
            .equalTo(showtime)

        timeslotRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (timeslotSnapshot in snapshot.children) {
                    timeslotSnapshot.ref.child("occupied").setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test123", "Error Updating Seat : $error")
            }
        })
    }

    fun getMovieTimeslots(cinemaLocation: String, cinemaName: String, theatreNumber: Int, callback: (List<SeatTimeslotModel?>) -> Unit){
        val timeslotRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")
            .child(NODE_SEATS)
            .child(NODE_LOWERBOX)
            .child("A")
            .child("1")
            .child(NODE_MOVIE_TIMESLOT)

        Log.d("test123", "$timeslotRef")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val timeslots: MutableList<SeatTimeslotModel?> = mutableListOf()

                for (timeslotSnapshot in dataSnapshot.children) {
                    val timeslot = timeslotSnapshot.getValue(SeatTimeslotModel::class.java)
                    timeslots.add(timeslot)

                    Log.d("test123", "time : ${timeslot?.time} occupied : ${timeslot?.occupied}")
                }

                callback(timeslots)
                Log.d("test123", "fun success1")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
                Log.d("test123", "fun cancelled: $databaseError")
            }
        }

        Log.d("test123", "fun end-1")
        timeslotRef.addValueEventListener(valueEventListener)
    }

    fun checkIfTimeslotExists(cinemaLocation: String, cinemaName: String, theatreNumber: Int, time: String): Task<Boolean> {
        val timeslotRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")
            .child(NODE_SEATS)
            .child("Lowerbox")
            .child("A/1")
            .child("seat_movie_timeslot")

        Log.d("test12", "$timeslotRef")

        return timeslotRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result

                for (timeslotSnapshot in dataSnapshot.children) {
                    val timeslot = timeslotSnapshot.getValue(SeatTimeslotModel::class.java)
                    if (timeslot?.time == time) {
                        return@continueWith true
                    }
                }
                false
            } else {
                Log.e("test12", "Error getting timeslot", task.exception)
                false
            }
        }
    }

    fun checkIfCinemaExists(cinemaLocation: String, cinemaName: String): Task<Boolean> {
        val cinemaRef = firebase_database.child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)

        return cinemaRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                dataSnapshot.exists()
            } else {
                false
            }
        }
    }

    fun checkIfCinemaTheatreExists(cinemaLocation: String, cinemaName: String, theatreNumber : Int): Task<Boolean> {
        val cinemaRef = firebase_database.child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")

        return cinemaRef.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                dataSnapshot.exists()
            } else {
                false
            }
        }
    }

    fun getMovieDetails(cinemaLocation: String, cinemaName: String, theatreNumber: Int, callback: (TheatreMovieModel?) -> Unit) {
        val theatreMovieDetailsRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child("Theatre$theatreNumber")
            .child(NODE_MOVIE_DETAILS)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val movieData = dataSnapshot.getValue<TheatreMovieModel>()
                callback(movieData)
                Log.d("test123", "fun success")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
                Log.d("test123", "fun cancelled: $databaseError")
            }
        }
        theatreMovieDetailsRef.addValueEventListener(valueEventListener)
    }

    fun getCinemaDetails(cinemaLocation: String, cinemaName: String, callback: (CinemaModel?) -> Unit) {
        val cinemaDetailsRef = firebase_database
            .child(NODE_CINEMA)
            .child(cinemaLocation)
            .child(cinemaName)
            .child(NODE_CINEMA_DETAILS)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cinemaData = dataSnapshot.getValue<CinemaModel>()
                Log.d("test123", "${cinemaData?.cinema_capacity}")
                callback(cinemaData)
                Log.d("test123", "fun success")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
                Log.d("test123", "fun cancelled: $databaseError")
            }
        }
        Log.d("test123", "fun end-1")
        cinemaDetailsRef.addValueEventListener(valueEventListener)
        Log.d("test123", "fun end-2")
    }

    fun createCinema_toDatabase(
        cinemaLocation: String,
        cinemaName : String,
        numOf_theatres : Int,

        lowerbox_width : Int,
        middlebox_width : Int,
        upperbox_width : Int,

        lowerbox_length : Int,
        middlebox_length : Int,
        upperbox_length : Int
    ) {
        val numRows = lowerbox_length + middlebox_length + upperbox_length
        val numColumns = lowerbox_width + middlebox_width + upperbox_width
        val cinemaRef = firebase_database.child(NODE_CINEMA)

        val cinemaCapacity = numRows * numColumns

        val cinemaDetails = CinemaModel(
            cinemaLocation,
            cinemaName,
            cinemaCapacity,
            upperbox_length,
            middlebox_length,
            lowerbox_length,
            upperbox_width,
            middlebox_width,
            lowerbox_width,
            "n/a")

        val cinemaRef0 = cinemaRef
            .child(cinemaLocation)
            .child(cinemaName)
            .child(NODE_CINEMA_DETAILS)
        cinemaRef0.setValue(cinemaDetails)

        for (theatre in 0 until numOf_theatres) {
            val theatreRef = cinemaRef
                .child(cinemaLocation)
                .child(cinemaName)
                .child("Theatre${theatre + 1}")

            val movie = TheatreMovieModel("", 0.0, "", "", "")
            theatreRef.child(NODE_MOVIE_DETAILS).setValue(movie)

            for (row in 0 until numRows) {
                val section =
                    if (row < lowerbox_length) NODE_LOWERBOX
                    else if (row < lowerbox_length + middlebox_length) NODE_MIDDLEBOX
                    else NODE_UPPERBOX

                val seatRef1 = theatreRef.child(NODE_SEATS)
                    .child(section)
                    .child("${(64+row+1).toChar()}")

                for (col in 0 until numColumns) {
                    val seat = SeatsModel("${(64+row+1).toChar()}${(col+1)}", "")
                    val seatRef2 = seatRef1.child("${(col+1)}")

                    seatRef2.setValue(seat.toMap())
                }
            }
        }
    }
}