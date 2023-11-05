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
import com.mp3.experiments.databinding.ActivitySeatSelectionBinding

class SeatSelectionActivity : AppCompatActivity() {

    private var left_width = 5
    private var middle_width = 14
    private var right_width = 5
    private var numColumns = left_width+middle_width+right_width
    private var numRows = 15

    private var seatSelected_count = 0

    private var seatStatus = Array(numRows) { BooleanArray(numColumns) }
    private var seatOccupied = Array(numRows) { BooleanArray(numColumns)}

    private lateinit var binding : ActivitySeatSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seatStatus = Array(numRows) { BooleanArray(numColumns) { false } }
        seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }

        val receivedBundle = intent.getBundleExtra("matrixBundle")
        val receivedMatrix = receivedBundle?.getSerializable("seatOccupied") as? Array<BooleanArray>

        if (receivedMatrix != null) {
            seatOccupied = receivedMatrix
        } else {
        }

        binding.gridLayout.rowCount = numRows
        binding.gridLayout.columnCount = numColumns

        for (row in 0 until numRows) {
            for (col in 0 until numColumns) {
                val seat = createSeatView(row, col, left_width, middle_width, right_width)
                binding.gridLayout.addView(seat)
            }
        }

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
            Log.d("test123", "$bundle")
            intent.putExtra("matrixBundle", bundle)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun createSeatView(row: Int, col: Int, left_width: Int, middle_width: Int, right_width: Int): ImageView {
        val seat = ImageView(this)

        if (seatOccupied[row][col]){
            Log.d("test123", "${seatOccupied[row][col]}")
            seat.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.filmflick_seat_unavailable))
        } else {
            Log.d("test123", "${seatOccupied[row][col]}")
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

        if (col == left_width-1 || col == left_width+middle_width-1){
            val rightMarginInPixels = (250).toInt() // Adjust the margin as needed
            params.rightMargin = rightMarginInPixels
        }

        if (row == (numRows/3) || row == (numRows/3)*2){
            val topMarginInPixels = (250).toInt() // Adjust the margin as needed
            params.topMargin = topMarginInPixels
        } else {
            val topMarginInPixels = (25).toInt() // Adjust the margin as needed
            params.topMargin = topMarginInPixels
        }


        seat.layoutParams = params

        return seat
    }

    private fun handleSeatSelection(row: Int, col: Int) {

        if (seatOccupied[row][col]) {
            Toast.makeText(this, "Seat ${(64+(numRows)-row).toChar()}${col+1} is occupied", Toast.LENGTH_SHORT).show()
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
            binding.tvSeatSelected.text = "${(64+(numRows)-row).toChar()}${col+1}"
        else {
            tempStr += ", ${(64+(numRows)-row).toChar()}${col+1}"
            binding.tvSeatSelected.text = tempStr
        }
    }

    private fun removeSelectedSeat(row: Int, col: Int){
        val tempStr = binding.tvSeatSelected.text.toString()
        val seat_label = "${(64+(numRows)-row).toChar()}${col+1}"
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