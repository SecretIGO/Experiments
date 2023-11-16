package com.mp3.experiments.ui.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mp3.experiments.R
import com.mp3.experiments.databinding.ActivitySigninBinding
import com.mp3.experiments.ui.views.admin.AdminActivity
import com.mp3.experiments.ui.views.customer.MovieTheatreSelectionActivity
import com.mp3.experiments.ui.views.customer.SeatSelectionActivity

class SigninActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 500

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 500

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.flSignup)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.hintSwipeup.startAnimation(fadeOut)
                        binding.hintSwipeup.visibility = View.GONE
                        if (binding.hintSwipeup.visibility == View.GONE){
                            binding.llSignup.startAnimation(fadeIn)
                            binding.llSignup.visibility = View.VISIBLE
                            binding.llSignin.startAnimation(fadeOut)
                            binding.llSignin.visibility = View.GONE
                        }
                        val layoutParams = binding.rlLogo.layoutParams as LinearLayout.LayoutParams
                        layoutParams.topMargin = dpToPx(100f).toInt()
                        binding.rlLogo.layoutParams = layoutParams
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.hintSwipeup.startAnimation(fadeIn)
                        binding.hintSwipeup.visibility = View.VISIBLE
                        if (binding.hintSwipeup.visibility == View.VISIBLE){
                            binding.llSignup.startAnimation(fadeOut)
                            binding.llSignup.visibility = View.GONE
                            binding.llSignin.startAnimation(fadeIn)
                            binding.llSignin.visibility = View.VISIBLE
                        }
                        val layoutParams = binding.rlLogo.layoutParams as LinearLayout.LayoutParams
                        layoutParams.topMargin = dpToPx(124f).toInt()
                        binding.rlLogo.layoutParams = layoutParams
                    }
                    else -> {
                        // Handle other states if needed
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.btnLogin.visibility =
                    if (slideOffset < 1.0) View.VISIBLE else View.GONE
                binding.btnSignup.visibility =
                    if (slideOffset >= 1.0) View.VISIBLE else View.GONE
            }
        })

        bottomSheetBehavior.peekHeight = spToPx(125f).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.btnLogin.setOnClickListener{
            if (binding.inputEmail.text.toString() == "admin"){
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                startActivity(Intent(this, MovieTheatreSelectionActivity::class.java))
            }

        }
    }

    fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }
}