package com.mp3.experiments.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mp3.experiments.R
import com.mp3.experiments.data.model.ComingSoonModel
import com.mp3.experiments.data.model.NowShowingModel
import com.mp3.experiments.databinding.ListItemComingSoonBinding
import com.mp3.experiments.databinding.ListItemNowShowingBinding

class ComingSoonAdapter (
    val list:ArrayList<ComingSoonModel>,
    private val clickListener: (ComingSoonModel) -> Unit
): RecyclerView.Adapter<ComingSoonAdapter.MyView>() {

    inner class MyView(val itemBinding: ListItemComingSoonBinding): RecyclerView.ViewHolder(itemBinding.root){
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
        return MyView(ListItemComingSoonBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        val cardView = holder.itemView.findViewById<CardView>(R.id.ItemMoviesComingSoon)

        val movieImage = cardView.findViewById<ImageView>(R.id.movieImageComingSoon)
        val tvName = cardView.findViewById<TextView>(R.id.tv_nameComingSoon)

        holder.itemBinding.movieImageComingSoon.setImageResource(list[position].comingsoon_image)
        holder.itemBinding.tvNameComingSoon.text = list[position].comingsoon_name
    }
    override fun getItemCount(): Int {
        return list.size
    }

}
