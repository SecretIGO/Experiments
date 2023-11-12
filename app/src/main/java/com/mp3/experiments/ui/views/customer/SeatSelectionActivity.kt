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
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivitySeatSelectionBinding

class SeatSelectionActivity : AppCompatActivity(), LoopCompleteCallbackInterface {

    private var upper_length = 0
    private var middle_length = 0
    private var lower_length = 0

    private var upper_width = 0
    private var middle_width = 0
    private var lower_width = 0

    var numRows = upper_length+middle_length+lower_length
    var numColumns = upper_width+middle_width+lower_width

    var theatreNumber = 0

    var seatStatus = Array(numRows) { BooleanArray(numColumns) { false } }
    var seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }
    var seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

    private lateinit var cinemaModel : CinemaModel

    private var seatSelected_count = 0
    private var time = ""

    private lateinit var theatreMovieModel : TheatreMovieModel
    private lateinit var binding : ActivitySeatSelectionBinding
    private lateinit var viewModel : CinemaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        theatreNumber = intent.getIntExtra("theatreNumber", 0)
        cinemaModel = intent.getParcelableExtra<CinemaModel>("cinemaModel")!!
        theatreMovieModel = intent.getParcelableExtra<TheatreMovieModel>("theatreMovieModel")!!
        time = intent.getStringExtra("timeslot")!!
        val receivedBundle = intent.getBundleExtra("matrixBundle")
        val receivedMatrix = receivedBundle?.getSerializable("seatOccupied") as? Array<BooleanArray>

        upper_length = cinemaModel.cinema_upperbox_length!!
        middle_length = cinemaModel.cinema_middlebox_length!!
        lower_length = cinemaModel.cinema_lowerbox_length!!

        upper_width = cinemaModel.cinema_upperbox_width!!
        middle_width = cinemaModel.cinema_middlebox_width!!
        lower_width = cinemaModel.cinema_lowerbox_width!!

        numRows = upper_length+middle_length+lower_length
        numColumns = upper_width+middle_width+lower_width

        binding.gridLayout.rowCount = numRows
        binding.gridLayout.columnCount = numColumns

        seatStatus = Array(numRows) { BooleanArray(numColumns) { false } }
        seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

        binding.tvMovieName.text = theatreMovieModel.movie_name
        binding.tvTheatreNumber.text = "Theatre$theatreNumber"
        binding.tvTimeslot.text = time

        if (receivedMatrix != null) {
            seatOccupied = receivedMatrix
        }

        Log.d("test123", "$numRows")

        for (row in 0 until  numRows) {
            for (col in 0 until numColumns) {
                val seat = createSeatView(row, col)
                binding.gridLayout.addView(seat)
            }
        }

        binding.btnClearInputs.setOnClickListener{
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.btnBuyTicket.setOnClickListener {
            if (seatSelected_count != 0){
                seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }

                for (row in 0 until numRows) {
                    for (col in 0 until numColumns) {
                        if (seatStatus[row][col]) {
                            seatSelected[row][col] = true

                        }
                        if (row == numRows - 1 && col == numColumns - 1) {
                            seatSelected = reverseRows(seatSelected)
                            onLoopCompleted()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You can't buy the air you know!", Toast.LENGTH_SHORT).show()
            }
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

    override fun onLoopCompleted() {
        val intent = Intent(this, BuyTicketActivity::class.java)
        val bundle = Bundle()
        val cinemaModel = cinemaModel
        bundle.putSerializable("seatSelected", seatSelected)
        intent.putExtra("matrixBundle", bundle)
        intent.putExtra("cinemaModel", cinemaModel )
        intent.putExtra("theatreMovieModel", theatreMovieModel)
        intent.putExtra("theatreNumber", theatreNumber)
        intent.putExtra("seatSelected_count", seatSelected_count)
        intent.putExtra("timeslot", time)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun reverseRows(array: Array<BooleanArray>): Array<BooleanArray> {
        val numRows = array.size
        val reversedArray = Array(numRows) { BooleanArray(array[0].size) }
        for (i in 0 until numRows) {
            reversedArray[i] = array[numRows - i - 1]
        }
        return reversedArray
    }
}