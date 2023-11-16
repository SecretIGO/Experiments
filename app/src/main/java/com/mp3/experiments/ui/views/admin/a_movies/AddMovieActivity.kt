package com.mp3.experiments.ui.views.admin.a_movies

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mp3.experiments.data.model.MovieModel
import com.mp3.experiments.data.viewmodel.MovieViewModel
import com.mp3.experiments.databinding.ActivityAddMovieBinding
import com.mp3.experiments.ui.views.admin.AdminActivity
import java.io.ByteArrayOutputStream
import java.util.Calendar


class AddMovieActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddMovieBinding
    private lateinit var viewModel : MovieViewModel
    private var imageUri : Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = MovieViewModel()

        binding.inputMovieStart.setOnClickListener{
            showDatePickerDialog(binding.inputMovieStart)
        }

        binding.inputMovieEnd.setOnClickListener{
            showDatePickerDialog(binding.inputMovieEnd)
        }

        binding.btnAddMovie.setOnClickListener {

            viewModel.checkIfMovieExist(binding.inputMovieName.text.toString())
                .addOnSuccessListener {exists ->
                if (exists) {
                    Toast.makeText(this, "${binding.inputMovieName.text.toString()} already exists!", Toast.LENGTH_SHORT).show()
                } else {
                    val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                    val movie = MovieModel(
                        binding.inputMovieName.text.toString(),
                        binding.inputMoviePrice.text.toString().toDouble(),
                        binding.inputMovieStart.text.toString(),
                        binding.inputMovieEnd.text.toString(),
                        binding.inputMovieDescription.text.toString(),
                        binding.inputMovieSynopsis.text.toString(),
                        ""
                    )
                    viewModel.createMovie_toDatabase(movie, baos.toByteArray())
                    Toast.makeText(this, "Added movie to database!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                }

            }

        }
    }

    private fun showDatePickerDialog(inputField : EditText) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                inputField.text = Editable.Factory.getInstance().newEditable(selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }
}