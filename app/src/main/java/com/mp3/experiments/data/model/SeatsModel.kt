package com.mp3.experiments.data.model

class SeatsModel (
    private val seat_label : String?,
    private val seat_movie_timeslot : String?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "seat_label" to seat_label,
            "seat_movie_timeslot" to seat_movie_timeslot
        )
    }
}