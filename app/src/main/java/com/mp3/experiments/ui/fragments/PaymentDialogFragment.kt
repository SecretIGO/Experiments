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
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.FragmentPaymentDialogBinding
import com.mp3.experiments.ui.views.customer.ReceiptActivity
import java.util.Locale

class PaymentDialogFragment : BottomSheetDialogFragment(), LoopCompleteCallbackInterface {

    private var totalPrice = 0.0
    private var theatreNumber = 0
    private var seatSelectedCount = 0
    private var timeslot = ""

    private lateinit var cinemaModel : CinemaModel
    private lateinit var theatreMovieModel : TheatreMovieModel

    private lateinit var binding : FragmentPaymentDialogBinding
    private lateinit var viewModel : CinemaViewModel

    private var isValid = true

    private var numRows = 0
    private var numColumns = 0

    var seatSelected = Array(numRows) { BooleanArray(numColumns) { false } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentDialogBinding.inflate(inflater, container, false)
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
                                    Log.d("test1234", "${(row + 64 + 1).toChar()}${col+1} : occupied")
                                    isValid = false
                                }

                                if (row == numRows - 1 && col == numColumns - 1) {
                                    if (isValid) {
                                        Toast.makeText(requireContext(), "All selected seats are not occupied", Toast.LENGTH_SHORT).show()
                                        onLoopCompleted()
                                    } else {
                                        if (seatSelectedCount == 1) {
                                            Toast.makeText(requireContext(), "Your selected seat might be taken by another user!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "One of your seats might have been taken by another user!", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "${(64 + row + 1).toChar()}${col+1} is selected", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(requireContext(), ReceiptActivity::class.java))
                }
            }
        }
    }
}