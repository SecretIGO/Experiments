package com.mp3.experiments.ui.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mp3.experiments.R
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.ActivitySigninBinding
import com.mp3.experiments.ui.views.admin.AdminActivity
import com.mp3.experiments.ui.views.customer.HomeActivity
import com.mp3.experiments.ui.views.customer.MovieTheatreSelectionActivity
import com.mp3.experiments.ui.views.customer.SeatSelectionActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SigninActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySigninBinding
    private lateinit var viewModel : UserViewModel
    private var imageUri : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = UserViewModel()
        viewModel.getAuthStates().observe(this@SigninActivity){
            renderUi(it)
        }

        bindAnimations()

        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.btnLogin.setOnClickListener{
            if (binding.inputEmail.text.toString() == "admin"){
                startActivity(Intent(this, AdminActivity::class.java))
            } else {

                if (signIn_CheckIfNoEmptyFields()){
                    viewModel.signIn_user(binding.inputEmail.text.toString(), binding.inputPassword.text.toString())
                    viewModel.getAuthStates().observe(this@SigninActivity){
                        auth_func(it)
                    }
                }
            }
        }

        binding.btnSignup.setOnClickListener{
            if (signUp_checkIfNoEmptyFields()){
                viewModel.signUp_user(
                    binding.inputSuEmail.text.toString(),
                    binding.inputSuPassword.text.toString()
                )

                val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                viewModel.getAuthStates().observe(this@SigninActivity) {
                    handleState(it, baos.toByteArray())
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.ivSelectedImage.setImageURI(imageUri)
    }

    private fun handleState(state : AuthenticationStates, baos : ByteArray) {
        when(state) {
            is AuthenticationStates.SignedUp -> viewModel.createUserRecord(
                binding.inputSuDisplayName.text.toString(),
                binding.inputSuNickname.text.toString(),
                binding.inputSuFirstName.text.toString(),
                binding.inputSuLastName.text.toString(),
                binding.inputSuEmail.text.toString(),
                binding.inputSuAge.text.toString().toInt(),
                getCurrentDateTime(),
                baos
            )

            is AuthenticationStates.ProfileUpdated -> {
                startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                finish()
            }
            else -> {}
        }
    }

    private fun auth_func(state : AuthenticationStates) {
        when(state) {
            AuthenticationStates.SignedIn -> {
                startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                finish()
            }
            else -> {}
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.isSignedIn()
    }

    private fun renderUi(states : AuthenticationStates) {
        when(states) {
            is AuthenticationStates.IsSignedIn -> {
                if(states.isSignedIn) {
                    startActivity(Intent(this@SigninActivity, HomeActivity::class.java))
                    finish()
                }
            }
            AuthenticationStates.SignedIn -> {
                startActivity(Intent(this@SigninActivity, SigninActivity::class.java))
                finish()
            }
            AuthenticationStates.Error -> {}
            else -> {}
        }
    }

    fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy, hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun signIn_CheckIfNoEmptyFields(): Boolean {
        var isValid = true

        if (binding.inputEmail.text.isNullOrEmpty()){
            binding.inputEmail.error = "Empty Field!"
            isValid = false
        }

        if (binding.inputPassword.text.isNullOrEmpty()){
            binding.inputPassword.error = "Empty Field!"
            isValid = false
        }


        return isValid
    }

    fun signUp_checkIfNoEmptyFields(): Boolean{
        var isValid = true

        if (binding.inputSuEmail.text.isNullOrEmpty()){
            binding.inputSuEmail.error = "Empty Field"
            isValid = false
        }

        if (binding.inputSuPassword.text.isNullOrEmpty()){
            binding.inputSuPassword.error = "Empty Field"
            isValid = false
        }

        if (binding.inputSuFirstName.text.isNullOrEmpty()){
            binding.inputSuFirstName.error = "Empty Field"
            isValid = false
        }

        if (binding.inputSuLastName.text.isNullOrEmpty()){
            binding.inputSuLastName.error = "Empty Field"
            isValid = false
        }

        if (binding.inputSuDisplayName.text.isNullOrEmpty()){
            binding.inputSuDisplayName.error = "Empty Field"
            isValid = false
        }
        if (binding.inputSuNickname.text.isNullOrEmpty()){
            binding.inputSuNickname.error = "Empty Field"
            isValid = false
        }
        if (binding.inputSuAge.text.isNullOrEmpty()){
            binding.inputSuAge.error = "Empty Field"
            isValid = false
        }

        return isValid

    }

    fun bindAnimations(){
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 500

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 500

        val fadeOut1 = AlphaAnimation(1f, 0f)
        fadeOut1.duration = 500
        fadeOut1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Animation start
            }

            override fun onAnimationEnd(animation: Animation) {
                // Animation end
                binding.llSignup.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Animation repeat
            }
        })

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
                        binding.llSignup.startAnimation(fadeOut1)
                        binding.llSignin.startAnimation(fadeIn)
                        binding.llSignin.visibility = View.VISIBLE
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

        bottomSheetBehavior.peekHeight = spToPx(120f).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}