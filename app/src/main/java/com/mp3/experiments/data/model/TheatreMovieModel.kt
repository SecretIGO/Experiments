package com.mp3.experiments.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TheatreMovieModel (
    val movie_name : String? = "",
    val movie_price : Double? = 0.0,
    val movie_date_active : String? = "",
    val movie_date_end : String? = "",
    val movie_image : String? = ""
) : Parcelable