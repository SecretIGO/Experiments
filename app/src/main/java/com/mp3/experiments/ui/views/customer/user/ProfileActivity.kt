package com.mp3.experiments.ui.views.customer.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mp3.experiments.R
import com.mp3.experiments.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            finish()
        }
    }
}