package com.mp3.experiments.ui.views.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ActionTypes
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.interfaces.TouchListener
import com.denzcoskun.imageslider.models.SlideModel
import com.mp3.experiments.R
import com.mp3.experiments.data.model.MovieItemsModel
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.databinding.ActivityHomeBinding
import com.mp3.experiments.ui.adapters.ComingSoonAdapter
import com.mp3.experiments.ui.adapters.NowShowingAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val imageSlider = binding.imageSlider // init imageSlider

        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel("https://www.cnet.com/a/img/resize/e40db3b1ecae8c7de688981520ac2c3d8c7c9617/hub/2023/08/16/23d96691-7b64-4113-81f8-71f8430f9aff/fast-x-peacock.jpg?auto=webp&width=1200"))
        imageList.add(SlideModel("https://assets-prd.ignimgs.com/2023/10/17/spidermanacrossthespiderversereview-blogroll-1685535068711-1697563405199.jpg"))
        imageList.add(SlideModel("https://www.barbie-themovie.com/images/share.jpg"))
        imageList.add(SlideModel("https://images.everyeye.it/img-notizie/the-marvels-marvel-annuncia-sorpresa-sceneggiatore-copione-scritto-8-mani-v3-627413.jpg"))
        imageList.add(SlideModel("https://roost.nbcuni.com/bin/viewasset.html/content/dam/Peacock/Landing-Pages/campaign/fnaf/fnaf-trailer-thumbnail.jpg/_jcr_content/renditions/original.JPG"))

        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                // You can listen here.
                println("normal")
            }

            override fun doubleClick(position: Int) {
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
                println("its double")
            }
        })

        imageSlider.setItemChangeListener(object : ItemChangeListener {
            override fun onItemChanged(position: Int) {
                //println("Pos: " + position)
            }
        })

        imageSlider.setTouchListener(object : TouchListener {
            override fun onTouched(touched: ActionTypes, position: Int) {
                if (touched == ActionTypes.DOWN) {
                    imageSlider.stopSliding()
                } else if (touched == ActionTypes.UP) {
                    imageSlider.startSliding(5000)
                }
            }
        })

        // Assuming you have references to the RecyclerViews in your layout
        val nowShowingRecyclerView = binding.rvNowShowing
        val comingSoonRecyclerView = binding.rvComingSoon

// Set up layout managers for RecyclerViews (you can choose LinearLayoutManager or GridLayoutManager)
        val layoutManagerNowShowing = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerComingSoon = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

// Set layout managers to RecyclerViews
        nowShowingRecyclerView.layoutManager = layoutManagerNowShowing
        comingSoonRecyclerView.layoutManager = layoutManagerComingSoon

        val nowShowingAdapter = NowShowingAdapter() // replace with your adapter class
        val comingSoonAdapter = ComingSoonAdapter() // replace with your adapter class


        val nowShowingMovies = listOf(
            MovieItemsModel("Movie 1", "400", R.drawable.movie1),
            MovieItemsModel("Movie 2", "450", R.drawable.movie2),
            MovieItemsModel("Movie 3", "500", R.drawable.movie3),
            MovieItemsModel("Movie 4", "550", R.drawable.movie4),
            MovieItemsModel("Movie 5", "500", R.drawable.movie5),
        )

        val comingSoonMovies = listOf(
            MovieItemsModel("Movie 6", "500", R.drawable.movie6),
            MovieItemsModel("Movie 7", "550", R.drawable.movie7),
            MovieItemsModel("Movie 8", "500", R.drawable.movie8),
            MovieItemsModel("Movie 9", "550", R.drawable.movie9),
            MovieItemsModel("Movie 10", "550", R.drawable.movie10),
        )

        nowShowingAdapter.nowShowingMovies = nowShowingMovies
        comingSoonAdapter.comingSoonMovies = comingSoonMovies

        // Set adapters to RecyclerViews
        nowShowingRecyclerView.adapter = nowShowingAdapter
        comingSoonRecyclerView.adapter = comingSoonAdapter

        setUpTimeslotSpinner()


    }

    private fun setUpTimeslotSpinner() {
        val timeslotList = listOf("10:00 AM", "2:00 PM", "6:00 PM", "9:00 PM")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeslotList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val timeslotSpinner = findViewById<Spinner>(R.id.timeslotSpinner)
        timeslotSpinner.adapter = adapter

        timeslotSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View

        }

    }

}


