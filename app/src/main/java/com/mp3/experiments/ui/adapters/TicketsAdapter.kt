package com.mp3.experiments.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.model.TheatreModel
import com.mp3.experiments.data.model.TicketModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.CinemaListItemBinding
import com.mp3.experiments.databinding.TicketListItemBinding
import com.mp3.experiments.ui.views.customer.MovieSelectionActivity

class TicketsAdapter (
    private val context : Context,
    private var ticketList: MutableList<TicketModel> = mutableListOf()
) : RecyclerView.Adapter<TicketsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TicketListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ticketList[position].let {
            holder.bind(it, position)
        }
    }

    fun addTickets(tickets: List<TicketModel>) {
        ticketList.addAll(tickets)
        notifyDataSetChanged()
    }

    class ViewHolder(

        private val context: Context,
        private val binding: TicketListItemBinding

    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: TicketModel, position: Int){
            Glide.with(context)
                .load(ticket.cinema_logo)
                .centerCrop()
                .into(binding.ivCinemaLogo)

            binding.tvCinemaLocation.text = ticket.cinema_location
            binding.tvCinemaName.text = ticket.cinema_name
            binding.tvMovieName.text = ticket.movie_name
            binding.tvMoviePrice.text = ticket.movie_price.toString()
            binding.tvTheatreNumber.text = ticket.theatre_number
            binding.tvMovieTimeslot.text = ticket.movie_time
            binding.tvMovieSeats.text = ticket.selected_seats
            binding.tvTicketIssueDate.text = ticket.ticket_buy_date
            binding.tvTotalAmount.text = ticket.total_price.toString()
        }
    }
}