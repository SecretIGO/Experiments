package com.mp3.experiments.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mp3.experiments.data.model.CinemaModel
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.CinemaListItemBinding
import com.mp3.experiments.ui.views.customer.MovieSelectionActivity

class CinemaAdapter (
    private val context : Context,
    private var cinemaList: MutableList<CinemaModel> = mutableListOf(),
    private var viewModel : CinemaViewModel
)  : RecyclerView.Adapter<CinemaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CinemaListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding, viewModel)
    }

    override fun getItemCount(): Int {
        return cinemaList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        cinemaList[position].let {
            holder.bind(it, position)
        }
    }

    fun addCinemas(cinemas: List<CinemaModel>) {
        cinemaList.addAll(cinemas)
        notifyDataSetChanged()
    }

    class ViewHolder(

        private val context: Context,
        private val binding: CinemaListItemBinding,
        private var viewModel : CinemaViewModel

    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cinema: CinemaModel, position: Int){
            binding.tvCinemaLocation.text = cinema.cinema_location
            binding.tvCinemaName.text = cinema.cinema_name
            binding.tvCinemaCapacity.text = cinema.cinema_capacity.toString() + " pax"

            Glide.with(context)
                .load(cinema.cinema_logo)
                .centerCrop()
                .into(binding.ivCinemaLogo)

            binding.itemCinemaItem.setOnClickListener {
                viewModel.checkIfCinemaExists(

                binding.tvCinemaLocation.text.toString(),
                binding.tvCinemaName.text.toString())

                .addOnSuccessListener { exists ->
                    if (exists) {
                        val intent = Intent(context, MovieSelectionActivity::class.java)
                        intent.putExtra("cinemaLocation", binding.tvCinemaLocation.text.toString())
                        intent.putExtra("cinemaName", binding.tvCinemaName.text.toString())
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Cinema : ${binding.tvCinemaName.text}\nLocation : ${binding.tvCinemaLocation.text}\nDoes Not Exist!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to check if cinema exists.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}