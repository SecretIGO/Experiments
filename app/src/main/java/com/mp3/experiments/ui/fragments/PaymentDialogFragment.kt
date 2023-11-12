package com.mp3.experiments.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.mp3.experiments.R
import com.mp3.experiments.databinding.FragmentPaymentDialogBinding
import java.util.Locale

class PaymentDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentPaymentDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val totalPrice = arguments?.getDouble(ARG_TOTAL_PRICE, 0.0) ?: 0.0

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.DialogAnimation
        }

        binding.tvTotalPrice.text = String.format(Locale.getDefault(), "%.2f", totalPrice)
    }

    companion object {
        private const val ARG_TOTAL_PRICE = "total_price"

        fun newInstance(totalPrice: Double): PaymentDialogFragment {
            val args = Bundle()
            args.putDouble(ARG_TOTAL_PRICE, totalPrice)

            val fragment = PaymentDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}