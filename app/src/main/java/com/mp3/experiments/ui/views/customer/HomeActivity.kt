package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ActionTypes
import com.denzcoskun.imageslider.constants.AnimationTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.interfaces.TouchListener
import com.denzcoskun.imageslider.models.SlideModel
import com.mp3.experiments.R
import com.mp3.experiments.data.states.AuthenticationStates
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.data.viewmodel.MovieViewModel
import com.mp3.experiments.data.viewmodel.UserViewModel
import com.mp3.experiments.databinding.ActivityHomeBinding
import com.mp3.experiments.databinding.ToolbarLayoutBinding
import com.mp3.experiments.ui.adapters.ComingSoonAdapter
import com.mp3.experiments.ui.adapters.NowShowingAdapter
import com.mp3.experiments.ui.adapters.TheatreMoviesAdapter
import com.mp3.experiments.ui.views.customer.user.ProfileActivity

class HomeActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    private lateinit var binding : ActivityHomeBinding
    private lateinit var toolbar : ToolbarLayoutBinding
    private lateinit var viewModel : MovieViewModel
    private lateinit var auth_vm : UserViewModel
    private lateinit var adapterNowShowing: NowShowingAdapter
    private lateinit var adapterComingSoon: ComingSoonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        toolbar = ToolbarLayoutBinding.bind(binding.root)
        setContentView(binding.root)

        toolbar.tvToolbarTitle.setText(R.string.app_name)
        toolbar.btnBack.visibility = View.GONE

        toolbar.llUsername.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        viewModel = MovieViewModel()
        auth_vm = UserViewModel()

        auth_vm.getUserProfile()
        auth_vm.getAuthStates().observe(this@HomeActivity){
            auth_func(it)
        }

        adapterNowShowing = NowShowingAdapter(this, ArrayList())
        binding.rvNowShowingItems.adapter = adapterNowShowing
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNowShowingItems.layoutManager = layoutManager

        viewModel.moviesNowShowing.observe(this, Observer {
            adapterNowShowing.addMovies(it)
        })
        viewModel.observeNowShowingMovies()

        adapterComingSoon = ComingSoonAdapter(this, ArrayList())
        binding.rvComingSoonItems.adapter = adapterComingSoon
        val layoutManager0 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvComingSoonItems.layoutManager = layoutManager0

        viewModel.moviesComingSoon.observe(this, Observer {
            adapterComingSoon.addMovies(it)
        })
        viewModel.observeComingSoonMovies()

        binding.btnBookMovie.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MovieTheatreSelectionActivity::class.java))
        }

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider) // init imageSlider

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
                if (touched == ActionTypes.DOWN){
                    imageSlider.stopSliding()
                } else if (touched == ActionTypes.UP ) {
                    imageSlider.startSliding(5000)
                }
            }
        })
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
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