package com.mp3.experiments.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MovieModel (
    val movie_name : String? = "",
    val movie_price : Double? = 0.0,
    val movie_date_active : String? = "",
    val movie_date_end : String? = "",
    val movie_description : String? = "",
    val movie_synopsis : String? = "",
    var movie_image : String? = ""
) : Parcelable