package com.mp3.experiments.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mp3.experiments.R
import com.mp3.experiments.data.interfaces.LoopCompleteCallbackInterface
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreMovieModel
import com.mp3.experiments.data.model.TicketModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.FragmentPaymentDialogBinding
import com.mp3.experiments.ui.views.customer.MovieSelectionActivity
import com.mp3.experiments.ui.views.customer.ReceiptActivity
import com.mp3.experiments.ui.views.customer.SeatSelectionActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PaymentDialogFragment : BottomSheetDialogFragment(), LoopCompleteCallbackInterface {

    private var totalPrice = 0.0
    private var theatreNumber = 0
    private var seatSelectedCount = 0
    private var timeslot = ""

    private lateinit var cinemaModel : CinemaModel
    private lateinit var theatreMovieModel : TheatreMovieModel

    private lateinit var binding : FragmentPaymentDialogBinding
    private lateinit var auth_vm : UserViewModel
    private lateinit var viewModel : CinemaViewModel

    private var isValid = true

    private var numRows = 0
    private var numColumns = 0

    var seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }
    var seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }
    var text_seatSelected = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentDialogBinding.inflate(inflater, container, false)
        auth_vm = UserViewModel()
        viewModel = CinemaViewModel()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        totalPrice = arguments?.getDouble(ARG_TOTAL_PRICE, 0.0)!!
        theatreNumber = arguments?.getInt(ARG_THEATRE_NUMBER, 0)!!
        seatSelectedCount = arguments?.getInt(ARG_SEAT_SELECTED_COUNT, 0)!!
        cinemaModel = arguments?.getParcelable<CinemaModel>(ARG_CINEMA_MODEL)!!
        theatreMovieModel = arguments?.getParcelable<TheatreMovieModel>(ARG_THEATRE_MOVIE_MODEL)!!
        val receivedBundle = arguments?.getBundle(ARG_MATRIX_BUNDLE)
        val receivedMatrix = receivedBundle?.getSerializable("seatSelected") as? Array<BooleanArray>
        timeslot = arguments?.getString(ARG_TIMESLOT)!!

        numRows = cinemaModel.cinema_upperbox_length!! + cinemaModel.cinema_middlebox_length!! + cinemaModel.cinema_lowerbox_length!!
        numColumns = cinemaModel.cinema_upperbox_width!! + cinemaModel.cinema_middlebox_width!! + cinemaModel.cinema_lowerbox_width!!

        seatOccupied = Array(numRows) { BooleanArray(numColumns) { false } }
        seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }

        if (receivedMatrix != null) {
            seatSelected = receivedMatrix
        }

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
        }

        binding.tvTotalPrice.text = String.format(Locale.getDefault(), "%.2f", totalPrice)
        binding.btnConfirmPayment.setOnClickListener {
            if (binding.inputPayment.text?.isNotBlank()!!){
                if (binding.inputPayment.text.toString().toDouble() >= totalPrice){

                    for (row in 0 until numRows) {
                        for (col in 0 until numColumns) {
                            viewModel.checkIfSeat_isOccupied(
                                seatSelected,
                                cinemaModel.cinema_location!!,
                                cinemaModel.cinema_name!!,
                                theatreNumber,
                                cinemaModel.cinema_lowerbox_length!!,
                                cinemaModel.cinema_middlebox_length!!,
                                row,
                                col,
                                timeslot
                            ).addOnSuccessListener { occupied ->

                                if (occupied) {
                                    isValid = false
                                }

                                if (seatSelected[row][col]){
                                    if (text_seatSelected == "")
                                        text_seatSelected = "${(64+row+1).toChar()}${col+1}"
                                    else {
                                        text_seatSelected += ", ${(64+row+1).toChar()}${col+1}"
                                    }

                                    Log.d("SeatSelectedTest", "$text_seatSelected are selected")
                                }

                                if (row == numRows - 1 && col == numColumns - 1) {
                                    if (isValid) {
                                        Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()
                                        onLoopCompleted()

                                    } else {
                                        Toast.makeText(requireContext(), "Failed! Returning to seat selection!", Toast.LENGTH_SHORT).show()
                                        if (seatSelectedCount == 1) {
                                            Toast.makeText(requireContext(), "Your selected seat might be taken by another user!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "One of your seats might have been taken by another user!", Toast.LENGTH_SHORT).show()
                                        }
                                        for (row1 in 0 until numRows) {
                                            for (col1 in 0 until numColumns) {
                                                viewModel.getSeatOccupied(
                                                    row1,
                                                    col1,
                                                    cinemaModel.cinema_location!!,
                                                    cinemaModel.cinema_name!!,
                                                    "Theatre$theatreNumber",
                                                    timeslot,
                                                    cinemaModel.cinema_lowerbox_length!!,
                                                    cinemaModel.cinema_middlebox_length!!
                                                ) { seatData ->
                                                    if (seatData != null) {
                                                        seatOccupied[row1][col1] = seatData.occupied!!
                                                        if (row1 == numRows - 1 && col1 == numColumns - 1) {

                                                            viewModel.getMovieDetails(cinemaModel.cinema_location!!, cinemaModel.cinema_name!!, theatreNumber) {movieModel ->
                                                                theatreMovieModel = movieModel!!
                                                                seatOccupied = reverseRows(seatOccupied)
                                                                val intent = Intent(requireContext(), SeatSelectionActivity::class.java)
                                                                val bundle = Bundle()
                                                                bundle.putSerializable("seatOccupied", seatOccupied)
                                                                intent.putExtra("matrixBundle", bundle)
                                                                intent.putExtra("cinemaModel", cinemaModel)
                                                                intent.putExtra("theatreMovieModel", theatreMovieModel)
                                                                intent.putExtra("theatreNumber", theatreNumber)
                                                                intent.putExtra("timeslot", timeslot)
                                                                context?.startActivity(intent)
                                                            }
                                                        }
                                                    } else {
                                                        Log.d("ntest", "seatData is null")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "You are ${totalPrice - binding.inputPayment.text.toString().toDouble()} short", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Do you even have money?", Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {
        private const val ARG_TOTAL_PRICE = "total_price"

        private const val ARG_THEATRE_NUMBER = "theatre_number"
        private const val ARG_SEAT_SELECTED_COUNT = "seat_selected_count"
        private const val ARG_CINEMA_MODEL = "cinema_model"
        private const val ARG_THEATRE_MOVIE_MODEL = "theatre_movie_model"
        private const val ARG_MATRIX_BUNDLE = "matrix_bundle"
        private const val ARG_TIMESLOT = "timeslot"

        fun newInstance(
            totalPrice: Double,
            theatreNumber: Int,
            seatSelectedCount: Int,
            cinemaModel: CinemaModel,
            theatreMovieModel: TheatreMovieModel,
            matrixBundle: Bundle,
            timeslot: String
            ): PaymentDialogFragment {

            val args = Bundle()
            args.putDouble(ARG_TOTAL_PRICE, totalPrice)
            args.putInt(ARG_THEATRE_NUMBER, theatreNumber)
            args.putInt(ARG_SEAT_SELECTED_COUNT, seatSelectedCount)
            args.putParcelable(ARG_CINEMA_MODEL, cinemaModel)
            args.putParcelable(ARG_THEATRE_MOVIE_MODEL, theatreMovieModel)
            args.putBundle(ARG_MATRIX_BUNDLE, matrixBundle)
            args.putString(ARG_TIMESLOT, timeslot)

            val fragment = PaymentDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onLoopCompleted() {
        for (row in 0 until numRows) {
            for (col in 0 until numColumns) {
                if (seatSelected[row][col]) {
                    viewModel.updateSeatOccupied(
                        cinemaModel.cinema_location!!,
                        cinemaModel.cinema_name!!,
                        theatreNumber,
                        cinemaModel.cinema_lowerbox_length!!,
                        cinemaModel.cinema_middlebox_length!!,
                        row,
                        col,
                        timeslot)
                }
            }
        }

        val currentDateTime = getCurrentDateTime()
        val intent = Intent(requireContext(), ReceiptActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtra("seatSelected", text_seatSelected)
        intent.putExtra("cinemaModel", cinemaModel)
        intent.putExtra("theatreMovieModel", theatreMovieModel)
        intent.putExtra("theatreNumber", theatreNumber)
        intent.putExtra("timeslot", timeslot)
        intent.putExtra("totalAmount", totalPrice)
        intent.putExtra("datetime", currentDateTime)

        val ticket = TicketModel(
            cinemaModel.cinema_location,
            cinemaModel.cinema_name,
            "Theatre$theatreNumber",
            text_seatSelected,
            seatSelectedCount,
            theatreMovieModel.movie_name,
            theatreMovieModel.movie_price,
            timeslot,
            totalPrice,
            getCurrentDateTime(),
            binding.inputPayment.text.toString().toDouble()
        )

        auth_vm.addTicket(ticket)

        startActivity(intent)
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy, hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
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