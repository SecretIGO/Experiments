package com.mp3.experiments.ui.views.admin.a_cinemas

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.mp3.experiments.data.viewmodel.CinemaViewModel
import com.mp3.experiments.databinding.ActivityAddCinemaBinding
import com.mp3.experiments.ui.views.admin.AdminActivity
import java.io.ByteArrayOutputStream

class AddCinemaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddCinemaBinding
    private lateinit var viewModel : CinemaViewModel
    private var imageUri : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCinemaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CinemaViewModel()

        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.btnAddCinema.setOnClickListener{

            viewModel.checkIfCinemaExists(
                binding.inputCinemaLocation.text.toString(),
                binding.inputCinemaName.text.toString())

            .addOnSuccessListener { exists ->
                Toast.makeText(this, "$exists", Toast.LENGTH_SHORT).show()
                if (exists) {
                    Toast.makeText(this, "${binding.inputCinemaName.text.toString()} at ${binding.inputCinemaLocation.text.toString()} Already Exists!", Toast.LENGTH_SHORT).show()
                } else {
                    val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                    viewModel.createCinema_toDatabase(
                        binding.inputCinemaLocation.text.toString(),
                        binding.inputCinemaName.text.toString(),
                        binding.inputNumOfTheatres.text.toString().toInt(),
                        binding.inputLowerWidth.text.toString().toInt(),
                        binding.inputMiddleWidth.text.toString().toInt(),
                        binding.inputUpperWidth.text.toString().toInt(),
                        binding.inputLowerLength.text.toString().toInt(),
                        binding.inputMiddleLength.text.toString().toInt(),
                        binding.inputUpperLength.text.toString().toInt(),
                        baos.toByteArray()
                    )
                    Toast.makeText(this, "Added Cinema to Database", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.ivSelectedImage.setImageURI(imageUri)
    }
}