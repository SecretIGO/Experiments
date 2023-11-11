package com.mp3.experiments.ui.views.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mp3.experiments.R
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.databinding.ActivityBuyTicketBinding
import com.mp3.experiments.ui.fragments.PaymentDialogFragment

class BuyTicketActivity : AppCompatActivity() {

    var numRows = 0
    var numColumns = 0

    var ticketPrice = 100
    var totalPrice = 0.0

    var seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }

    private lateinit var binding : ActivityBuyTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val theatreNumber = intent.getIntExtra("theatreNumber", 0)!!
        val seatSelected_count = intent.getIntExtra("seatSelected_count", 0)
        val cinemaModel = intent.getParcelableExtra<CinemaModel>("cinemaModel")
        val theatreMovieModel = intent.getParcelableExtra<TheatreMovieModel>("theatreMovieModel")
        val receivedBundle = intent.getBundleExtra("matrixBundle")
        val receivedMatrix = receivedBundle?.getSerializable("seatSelected") as? Array<BooleanArray>
        val time = intent.getStringExtra("timeslot")!!

        numRows = cinemaModel?.cinema_upperbox_length!! + cinemaModel.cinema_middlebox_length!! + cinemaModel.cinema_lowerbox_length!!
        numColumns = cinemaModel.cinema_upperbox_width!! + cinemaModel.cinema_middlebox_width!! + cinemaModel.cinema_lowerbox_width!!

        seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }

        if (receivedMatrix != null) {
            seatSelected = receivedMatrix
        }

        for (row in 0 until numRows) {
            for (col in 0 until numColumns) {
                if (seatSelected[row][col] == true) {
                    var tempStr = binding.tvSeatSelected.text.toString()

                    if (tempStr == "n/a")
                        binding.tvSeatSelected.text = "${(64+row+1).toChar()}${col+1}"
                    else {
                        tempStr += ", ${(64+row+1).toChar()}${col+1}"
                        binding.tvSeatSelected.text = tempStr
                    }
                }
            }
        }

        binding.tvCinemaLocation.text = cinemaModel.cinema_location
        binding.tvCinemaName.text = cinemaModel.cinema_name
        binding.tvTheatreNumber.text = "Theatre$theatreNumber"
        binding.tvMovieName.text = theatreMovieModel?.movie_name.toString()
        binding.tvMoviePrice.text = theatreMovieModel?.movie_price.toString()
        binding.tvNumOfSelectedSeats.text = seatSelected_count.toString()
        binding.tvTimeslot.text = time

        calculate_ticketPrice(seatSelected_count, theatreMovieModel?.movie_price!!)
        val formattedTotalPrice = String.format("%.2f", totalPrice)

        binding.tvTotalPrice.text = formattedTotalPrice

        binding.btnConfirmSeats.setOnClickListener{
            showDialog()
        }
    }

    fun calculate_ticketPrice(seatSelected_count : Int, moviePrice : Double){
        totalPrice = seatSelected_count * moviePrice
    }

    fun showDialog() {
        val dialogFragment = PaymentDialogFragment()
        dialogFragment.show(supportFragmentManager, PaymentDialogFragment::class.java.simpleName)
    }
}