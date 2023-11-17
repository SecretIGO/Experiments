package com.mp3.experiments.ui.views.customer.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mp3.experiments.R
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.ActivityTicketsBinding
import com.mp3.experiments.databinding.ToolbarLayoutBinding
import com.mp3.experiments.ui.adapters.TheatreMoviesAdapter
import com.mp3.experiments.ui.adapters.TicketsAdapter

class TicketsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTicketsBinding
    private lateinit var auth_vm : UserViewModel
    private lateinit var adapter : TicketsAdapter
    private lateinit var toolbar : ToolbarLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketsBinding.inflate(layoutInflater)
        toolbar = ToolbarLayoutBinding.bind(binding.root)
        setContentView(binding.root)

        toolbar.tvToolbarTitle.setText("Tickets")
        toolbar.btnBack.setOnClickListener{
            finish()
        }

        auth_vm = UserViewModel()

        auth_vm.getUserProfile()
        auth_vm.getAuthStates().observe(this@TicketsActivity){
            auth_func(it)
        }

        adapter = TicketsAdapter(this, ArrayList())
        binding.rvTickets.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.rvTickets.layoutManager = layoutManager

        auth_vm.tickets.observe(this, Observer {
            adapter.addTickets(it)
        })
        auth_vm.observeTickets()
    }

    private fun auth_func(authenticationStates: AuthenticationStates) {
        when (authenticationStates) {
            is AuthenticationStates.Default -> {
                toolbar.tvUsername.text = authenticationStates.user?.user_details?.username
                Glide.with(this)
                    .load(authenticationStates.user?.user_details?.user_profilePicture)
                    .centerCrop()
                    .into(toolbar.ivUserProfile)
            }
            AuthenticationStates.LogOut -> super.finish()
            else -> {}
        }
    }
}