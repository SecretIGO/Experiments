package com.mp3.experiments.ui.views.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)

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

        val button = findViewById<Button>(R.id.btn_home)
        button.setOnClickListener {
            // Handle the button click, start a new activity
            val intent = Intent(this@HomeActivity, ReceiptActivity::class.java)
            startActivity(intent)
        }

    }
}
