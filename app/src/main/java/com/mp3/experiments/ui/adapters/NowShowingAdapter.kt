package com.mp3.experiments.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mp3.experiments.R
import com.mp3.experiments.data.model.NowShowingModel
import com.mp3.experiments.databinding.ListItemNowShowingBinding

class NowShowingAdapter(
    val list:ArrayList<NowShowingModel>,
    private val clickListener: (NowShowingModel) -> Unit
):RecyclerView.Adapter<NowShowingAdapter.MyView>() {

    companion object {
        val timeSlots = listOf("", "10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM", "6:00 PM", "8:00 PM")
    }

    inner class MyView(val itemBinding: ListItemNowShowingBinding):RecyclerView.ViewHolder(itemBinding.root){
        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener(list[position])
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(ListItemNowShowingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        val cardView = holder.itemView.findViewById<CardView>(R.id.ItemMoviesNowShowing)

        val movieImage = cardView.findViewById<ImageView>(R.id.movieImageNowShowing)
        val tvName = cardView.findViewById<TextView>(R.id.tv_nameNowShowing)
        val tvPrice = cardView.findViewById<TextView>(R.id.tv_priceNowShowing)

        holder.itemBinding.movieImageNowShowing.setImageResource(list[position].nowshowing_image)
        holder.itemBinding.tvNameNowShowing.text = list[position].nowshowing_name
        holder.itemBinding.tvPriceNowShowing.text = list[position].nowshowing_price.toString()

        val spinner = cardView.findViewById<Spinner>(R.id.timeslotSpinner)
        val adapter = ArrayAdapter<String>(holder.itemView.context, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun getItemCount(): Int {
        return list.size
    }

}