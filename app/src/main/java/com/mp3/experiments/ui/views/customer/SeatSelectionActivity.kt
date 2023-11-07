package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mp3.experiments.R
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivitySeatSelectionBinding

class SeatSelectionActivity : AppCompatActivity() {

    private var upper_length = 0
    private var middle_length = 0
    private var lower_length = 0

    private var upper_width = 0
    private var middle_width = 0
    private var lower_width = 0

    var numRows = upper_length+middle_length+lower_length
    var numColumns = upper_width+middle_width+lower_width

    var seatStatus = Array(numRows) { BooleanArray(numColumns) { false } }
    var seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

    private var seatSelected_count = 0

    private lateinit var binding : ActivitySeatSelectionBinding
    private lateinit var viewModel : CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        val cinemaLocation = intent.getStringExtra("cinemaLocation")
        val cinemaName = intent.getStringExtra("cinemaName")

        if (cinemaLocation != null && cinemaName != null) {

            viewModel.getCinemaDetails(cinemaLocation, cinemaName) { cinemaModel ->

                if (cinemaModel != null) {
                    Log.d("test123", "vm reach")

                    upper_length = cinemaModel.cinema_upperbox_length!!
                    middle_length = cinemaModel.cinema_middlebox_length!!
                    lower_length = cinemaModel.cinema_lowerbox_length!!

                    upper_width = cinemaModel.cinema_upperbox_width!!
                    middle_width = cinemaModel.cinema_middlebox_width!!
                    lower_width = cinemaModel.cinema_lowerbox_width!!

                    numRows = upper_length+middle_length+lower_length
                    numColumns = upper_width+middle_width+lower_width

                    seatStatus = Array(numRows) { BooleanArray(numColumns) { false } }
                    seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

                    binding.gridLayout.rowCount = numRows
                    binding.gridLayout.columnCount = numColumns

                    for (row in numRows downTo 0) {
                        for (col in 0 until numColumns) {
                            Log.d("test234", "$row$col")
                            viewModel.updateSeatOccupied(
                                row,
                                col,
                                cinemaLocation,
                                cinemaName,
                                "Theatre1",
                                "",
                                lower_length,
                                middle_length,
                                numRows,
                                numColumns
                            ) { seatData ->

                                if (seatData != null) {
                                    seatOccupied[numRows - row - 1][col] = seatData.occupied!!
                                    val seat = createSeatView(numRows - row - 1, col)
                                    binding.gridLayout.addView(seat)
                                } else {
                                    Log.d("ntest", "seatData is null")
                                }
                            }
                        }
                    }

                    Log.d("test123", "$upper_length $middle_length $lower_length")
                } else {
                    Log.d("test123", "vm is null")
                }
            }
        }

        Log.d("test123", "fun call end")

//        val receivedBundle = intent.getBundleExtra("matrixBundle")
//        val receivedMatrix = receivedBundle?.getSerializable("seatOccupied") as? Array<BooleanArray>
//
//        if (receivedMatrix != null) {
//            seatOccupied = receivedMatrix
//        } else {
//        }

        binding.btnClearInputs.setOnClickListener{
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.btnBuyTicket.setOnClickListener {
            for (row in 0 until numRows) {
                for (col in 0 until numColumns) {
                    if (seatStatus[row][col]) {
                        seatOccupied[row][col] = true
                    }
                }
            }

            val intent = Intent(this, SeatSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("seatOccupied", seatOccupied)
            intent.putExtra("matrixBundle", bundle)
            intent.putExtra("cinemaLocation", cinemaLocation)
            intent.putExtra("cinemaName", cinemaName)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun createSeatView(
        row: Int,
        col: Int,
    ): ImageView {
        val seat = ImageView(this)

        if (seatOccupied[row][col]){
            seat.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.filmflick_seat_unavailable))
        } else {
            seat.setImageResource(R.drawable.filmflick_seat_available)
        }

        seat.setOnClickListener {
            handleSeatSelection(row, col)
        }

        val seatSizeInDp = 45
        val seatSizeInPixels = (seatSizeInDp * resources.displayMetrics.density).toInt()

        val params = GridLayout.LayoutParams()
        params.rowSpec = GridLayout.spec(row)
        params.columnSpec = GridLayout.spec(col)
        params.width = seatSizeInPixels
        params.height = seatSizeInPixels

        if (col == upper_width-1 || col == upper_width+middle_width-1){
            val rightMarginInPixels = (250).toInt() // Adjust the margin as needed
            params.rightMargin = rightMarginInPixels
        }

        if (row == upper_length || row == upper_length+middle_length) {
            val topMarginInPixels = (250).toInt() // Adjust the margin as needed
            params.topMargin = topMarginInPixels
        } else {
            val topMarginInPixels = (25).toInt() // Adjust the margin as needed
            params.topMargin = topMarginInPixels
        }


        seat.layoutParams = params

        return seat
    }

    private fun handleSeatSelection(
        row: Int,
        col: Int) {

        Log.d("test123", "$numRows")
        Log.d("test123", "$numColumns")

        if (seatOccupied[row][col]) {
            Toast.makeText(this, "Seat ${(64+numRows-row).toChar()}${col+1} is occupied", Toast.LENGTH_SHORT).show()
        } else {
            if (seatStatus[row][col]) {
                seatStatus[row][col] = false
                seatSelected_count--
                binding.tvSeatSelectedCount.text = seatSelected_count.toString()
                removeSelectedSeat(row, col)
                (binding.gridLayout.getChildAt(row * numColumns + col) as ImageView)
                    .setImageResource(R.drawable.filmflick_seat_available)
            } else {
                seatStatus[row][col] = true
                seatSelected_count++
                binding.tvSeatSelectedCount.text = seatSelected_count.toString()
                addSelectedSeat(row, col)
                (binding.gridLayout.getChildAt(row * numColumns + col) as ImageView)
                    .setImageResource(R.drawable.filmflick_seat_selected)
            }
        }
    }

    private fun addSelectedSeat(row: Int, col: Int){
        var tempStr = binding.tvSeatSelected.text.toString()

        if (tempStr == "n/a")
            binding.tvSeatSelected.text = "${(64+numRows-row).toChar()}${col+1}"
        else {
            tempStr += ", ${(64+numRows-row).toChar()}${col+1}"
            binding.tvSeatSelected.text = tempStr
        }
    }

    private fun removeSelectedSeat(row: Int, col: Int){
        val tempStr = binding.tvSeatSelected.text.toString()
        val seat_label = "${(64+numRows-row).toChar()}${col+1}"
        val newStr =
            if (tempStr.startsWith(seat_label)) {
                tempStr.replace(Regex("$seat_label,?\\s*"), "").trim()
            } else {
                tempStr.replaceFirst(Regex("(^|,) *$seat_label *"), "").trim()
            }

        if (newStr == ""){
            binding.tvSeatSelected.text = "n/a"
        } else {
            binding.tvSeatSelected.text = newStr
        }
    }
}