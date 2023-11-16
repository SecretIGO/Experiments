package com.mp3.experiments.ui.views.customer.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.mp3.experiments.R
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.ActivityProfileBinding
import com.mp3.experiments.ui.views.SigninActivity
import com.mp3.experiments.ui.views.customer.ReceiptActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth_vm : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth_vm = UserViewModel()
        auth_vm.getUserProfile()
        auth_vm.getAuthStates().observe(this@ProfileActivity){
            auth_func(it)
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnLogout.setOnClickListener {
            auth_vm.logOut()
        }
    }

    private fun auth_func(authenticationStates: AuthenticationStates) {
        when (authenticationStates) {
            is AuthenticationStates.Default -> {
                binding.tvUsername.text = authenticationStates.user?.user_details?.username
                binding.tvNickname.text = authenticationStates.user?.user_details?.user_nickname
                binding.tvFullname.text = "${authenticationStates.user?.user_details?.user_firstname} ${authenticationStates.user?.user_details?.user_lastname}"
                binding.tvEmail.text = authenticationStates.user?.user_details?.user_email
                binding.tvAge.text = authenticationStates.user?.user_details?.user_age.toString()
                binding.tvAccountCreatedDate.text = authenticationStates.user?.user_details?.account_date_created
                binding.tvTicketsBought.text = authenticationStates.user?.user_details?.tickets_bought.toString()

                Glide.with(this)
                    .load(authenticationStates.user?.user_details?.user_profilePicture)
                    .centerCrop()
                    .into(binding.ivProfilePicture)
            }
            AuthenticationStates.LogOut -> {
                val intent = Intent(this, SigninActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            else -> {}
        }
    }
}