package com.mp3.experiments.data.model

class SeatsModel (
    private val seat_label : String?,
    private val seat_movie_timeslot : String?,
    private val seat_occupied : Boolean?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "seat_label" to seat_label,
            "seat_movie_timeslot" to seat_movie_timeslot,
            "seat_occupied" to seat_occupied
        )
    }
}