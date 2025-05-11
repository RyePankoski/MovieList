package com.example.movielist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddMovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            backToFirst()
        }
    }

    private fun backToFirst() {
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val yearEditText = findViewById<EditText>(R.id.yearEditText)
        val genreEditText = findViewById<EditText>(R.id.genreEditText)
        val ratingEditText = findViewById<EditText>(R.id.ratingEditText)

        val title = titleEditText.text.toString()
        val year = yearEditText.text.toString()
        val genre = genreEditText.text.toString()
        val rating = ratingEditText.text.toString()

        if (title.isNotEmpty() && year.isNotEmpty() && genre.isNotEmpty() && rating.isNotEmpty()) {
            setMovieInfo(title, year, genre, rating)
        }
    }

    private fun setMovieInfo(title: String, year: String, genre: String, rating: String) {
        val intent = Intent()
        intent.putExtra("TITLE", title)
        intent.putExtra("YEAR", year)
        intent.putExtra("GENRE", genre)
        intent.putExtra("RATING", rating)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}