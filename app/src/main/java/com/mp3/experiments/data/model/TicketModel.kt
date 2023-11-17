package com.mp3.experiments.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TicketModel(
    val cinema_location : String? = "",
    val cinema_name : String? = "",
    val theatre_number : String? = "",
    val selected_seats : String = "",
    val number_of_selected_seats : Int? = 0,

    val movie_name : String? = "",
    val movie_price : Double? = 0.0,
    val movie_time : String = "",

    val total_price : Double? = 0.0,
    val ticket_buy_date : String? = "",
    val payment : Double? = 0.0,

    val cinema_logo : String? = ""
)  : Parcelable