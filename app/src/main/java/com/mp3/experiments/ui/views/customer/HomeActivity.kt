package com.mp3.experiments.ui.views.customer

import android.content.Intent
import android.icu.text.IDNA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ActionTypes
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.interfaces.TouchListener
import com.denzcoskun.imageslider.models.SlideModel
import com.mp3.experiments.R
import com.mp3.experiments.data.model.ComingSoonModel
import com.mp3.experiments.data.model.HomeMovieModel
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.model.NowShowingModel
import com.mp3.experiments.databinding.ActivityHomeBinding
import com.mp3.experiments.ui.adapters.ComingSoonAdapter
import com.mp3.experiments.ui.adapters.MovieCarouselAdapter
import com.mp3.experiments.ui.adapters.NowShowingAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var nowshowingadapter: NowShowingAdapter
    private lateinit var comingsoonadapter: ComingSoonAdapter
    private lateinit var nowshowinglist: ArrayList<NowShowingModel>
    private lateinit var comingsoonlist: ArrayList<ComingSoonModel>
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val movielist = ArrayList<HomeMovieModel>()
        movielist.add(HomeMovieModel(R.drawable.movie1, "Fast X"))
        movielist.add(HomeMovieModel(R.drawable.movie2, "Spider-Man"))
        movielist.add(HomeMovieModel(R.drawable.movie3, "Barbie"))
        movielist.add(HomeMovieModel(R.drawable.movie4, "The Marvels"))
        movielist.add(HomeMovieModel(R.drawable.movie5, "Five Nights at Freddy's"))

        val adapter = MovieCarouselAdapter(movielist)

        binding.apply {
            carouselRecyclerView.adapter = adapter
            carouselRecyclerView.set3DItem(true)
            carouselRecyclerView.setAlpha(true)
            carouselRecyclerView.setInfinite(true)
        }


        binding.rvNowShowing.setHasFixedSize(true)

        nowshowinglist = ArrayList()
        nowshowinglist.add(NowShowingModel(R.drawable.movie1, "Fast X", "200"))
        nowshowinglist.add(NowShowingModel(R.drawable.movie2, "Spider-Man", "200"))
        nowshowinglist.add(NowShowingModel(R.drawable.movie3, "Barbie", "200"))
        nowshowinglist.add(NowShowingModel(R.drawable.movie4, "The Marvels", "200"))
        nowshowinglist.add(NowShowingModel(R.drawable.movie5, "Five Nights at Freddy's", "200"))

        nowshowingadapter = NowShowingAdapter(nowshowinglist) { selectedNowShowing ->

            val intent = Intent(this@HomeActivity, MovieTheatreSelectionActivity::class.java)
            startActivity(intent)
        }

        binding.rvNowShowing.adapter = nowshowingadapter
        binding.rvNowShowing.layoutManager = LinearLayoutManager(this)


        binding.rvComingSoon.setHasFixedSize(true)

        comingsoonlist = ArrayList()
        comingsoonlist.add(ComingSoonModel(R.drawable.movie6, "Despicable Me 4"))
        comingsoonlist.add(ComingSoonModel(R.drawable.movie7, "Deadpool 3"))
        comingsoonlist.add(ComingSoonModel(R.drawable.movie8, "Kingdom of The Planet of The Apes"))
        comingsoonlist.add(ComingSoonModel(R.drawable.movie9, "Godzilla X Kong The New Empire"))
        comingsoonlist.add(ComingSoonModel(R.drawable.movie10, "Sonic the Hedgehog 3"))

        comingsoonadapter = ComingSoonAdapter(comingsoonlist) { selectedComingSoon ->

            Toast.makeText(this, "Coming Soon... Stay Tuned!", Toast.LENGTH_SHORT).show()
        }

        binding.rvComingSoon.adapter = comingsoonadapter
        binding.rvComingSoon.layoutManager = LinearLayoutManager(this)

    }
}