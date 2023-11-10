package com.mp3.experiments.data.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.nodes.NODE_CINEMA
import com.mp3.experiments.data.nodes.NODE_MOVIES

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
}