package com.mp3.experiments.ui.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // work in progress

        binding.btnLogin.setOnClickListener{
            if (binding.inputEmail.text.toString() == "admin"){
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                startActivity(Intent(this, MovieTheatreSelectionActivity::class.java))
            }

        }
    }
}