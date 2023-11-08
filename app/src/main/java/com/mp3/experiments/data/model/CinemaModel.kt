package com.mp3.experiments.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CinemaModel (
    val cinema_location : String?= "",

    val cinema_name : String? = "",
    val cinema_capacity : Int? = 0,

    val cinema_upperbox_length : Int? = 0,
    val cinema_middlebox_length : Int? = 0,
    val cinema_lowerbox_length : Int? = 0,
    val cinema_upperbox_width : Int? = 0,
    val cinema_middlebox_width : Int? = 0,
    val cinema_lowerbox_width : Int? = 0,

    val cinema_logo : String? = ""
) : Parcelable