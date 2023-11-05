package com.mp3.experiments.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.SeatsModel
import com.mp3.experiments.data.nodes.NODE_CINEMA
import com.mp3.experiments.data.nodes.NODE_LOWERBOX
import com.mp3.experiments.data.nodes.NODE_MIDDLEBOX
import com.mp3.experiments.data.nodes.NODE_SEATS
import com.mp3.experiments.data.nodes.NODE_UPPERBOX

class CinemaViewModel : ViewModel() {
    private val firebase_database = Firebase.database.reference

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
        val seatsRef = firebase_database.child(NODE_CINEMA)

        for (theatre in 0 until numOf_theatres) {
            val theatreRef = seatsRef
                .child(cinemaLocation)
                .child(cinemaName)
                .child("Theatre${theatre + 1}")

            for (row in 0 until numRows) {
                val section =
                    if (row < lowerbox_length) NODE_LOWERBOX
                    else if (row < lowerbox_length + middlebox_length) NODE_MIDDLEBOX
                    else NODE_UPPERBOX

                val seatRef1 = theatreRef.child(NODE_SEATS)
                    .child(section)
                    .child("${(64+row+1).toChar()}")

                for (col in 0 until numColumns) {
                    val seat = SeatsModel("${(64+row+1).toChar()}${(col+1)}", "MovieTimeslot", false)
                    val seatRef2 = seatRef1.child("${(col+1)}")

                    seatRef2.setValue(seat.toMap())
                }
            }
        }
    }
}