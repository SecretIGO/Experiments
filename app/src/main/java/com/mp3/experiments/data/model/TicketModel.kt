package com.mp3.experiments.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TicketModel(
    val cinema_name : String? = "",
    val theatre_number : Int? = 0,
    val seat_row : String? = "",
    val seat_col : Int = 0,

    val movie_name : String? = "",
    val movie_price : Double? = 0.0,
    val movie_time : String = "",

    val total_price : Double? = 0.0,
    val ticket_buy_date : String? = "",
    val payment : Double? = 0.0
)  : Parcelable