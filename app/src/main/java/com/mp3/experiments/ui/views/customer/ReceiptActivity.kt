package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.bumptech.glide.Glide
import com.mp3.experiments.R
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.databinding.ActivityReceiptBinding

class ReceiptActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    private lateinit var binding : ActivityReceiptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text_seatSelected = intent.getStringExtra("seatSelected")
        val theatreNumber = intent.getIntExtra("theatreNumber", 0)!!
        val cinemaModel = intent.getParcelableExtra<CinemaModel>("cinemaModel")
        val theatreMovieModel = intent.getParcelableExtra<TheatreMovieModel>("theatreMovieModel")
        val timeslot = intent.getStringExtra("timeslot")!!
        val totalPrice = intent.getDoubleExtra("totalAmount", 0.0)
        val datetime = intent.getStringExtra("datetime")

        binding.tvMovieSeats.text = text_seatSelected
        binding.tvCinemaName.text = cinemaModel?.cinema_name
        binding.tvCinemaLocation.text = cinemaModel?.cinema_location
        binding.tvMovieName.text = theatreMovieModel?.movie_name
        binding.tvMoviePrice.text = theatreMovieModel?.movie_price.toString() + " php"
        binding.tvTheatreNumber.text = "Theatre$theatreNumber"
        binding.tvMovieTimeslot.text = timeslot
        Glide.with(this)
            .load(cinemaModel?.cinema_logo)
            .centerCrop()
            .into(binding.ivCinemaLogo)

        val formattedTotalPrice = String.format("%.2f", totalPrice)
        binding.tvTotalAmount.text = formattedTotalPrice
        binding.tvTicketIssueDate.text = datetime

        binding.btnBuyAnotherMovie.setOnClickListener {
            val intent = Intent(this, MovieSelectionActivity::class.java)
            intent.putExtra("cinemaLocation", binding.tvCinemaLocation.text.toString())
            intent.putExtra("cinemaName", binding.tvCinemaName.text.toString())
            startActivity(intent)
        }

        binding.btnDone.setOnClickListener{
            startActivity(Intent(this, MovieTheatreSelectionActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}