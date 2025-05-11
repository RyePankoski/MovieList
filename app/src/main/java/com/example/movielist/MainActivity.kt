package com.example.movielist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private var movieList = ArrayList<Movie>()
    private lateinit var myPlace: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Debug: Log the start of onCreate
        Log.d("MOVIELIST", "onCreate started")

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        movieAdapter = MovieAdapter(movieList)
        recyclerView.adapter = movieAdapter

        // Setup file directory
        myPlace = this.filesDir
        Log.d("MOVIELIST", "File directory: ${myPlace.absolutePath}")

        // Read the initial file
        readFile()

        // Add swipe to delete
        val itemTouchHelper = ItemTouchHelper(movieAdapter.swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Setup button click listeners
        val addButton = findViewById<Button>(R.id.addMovieButton)
        val saveButton = findViewById<Button>(R.id.saveListButton)
        val sortButton = findViewById<ImageButton>(R.id.sortButton)

        addButton.setOnClickListener { startSecond() }
        saveButton.setOnClickListener { saveList() }

        sortButton.setOnClickListener { view ->
            showSortMenu(view)
        }

        Log.d("MOVIELIST", "onCreate completed")
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.ratingSort -> {
                    Log.d("MOVIELIST", "onOptions: rating sort")
                    movieList.sortBy { it.rating }
                    movieAdapter.notifyDataSetChanged()
                    true
                }
                R.id.yearSort -> {
                    Log.d("MOVIELIST", "onOptions: year sort")
                    movieList.sortBy { it.year }
                    movieAdapter.notifyDataSetChanged()
                    true
                }
                R.id.genreSort -> {
                    Log.d("MOVIELIST", "onOptions: genre sort")
                    movieList.sortBy { it.genre }
                    movieAdapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    // Function to read the file
    private fun readFile() {
        try {
            val file = File(myPlace, "MOVIELIST.csv")
            Log.d("MOVIELIST", "File exists: ${file.exists()}")

            if (file.exists()) {
                val fileInputStream = FileInputStream(file)
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var line: String?
                movieList.clear()

                var count = 0
                while (bufferedReader.readLine().also { line = it } != null) {
                    val tokens = line!!.split(",")
                    if (tokens.size >= 4) {
                        val title = tokens[0]
                        val year = tokens[1]
                        val genre = tokens[2]
                        val rating = tokens[3]
                        movieList.add(Movie(title, year, genre, rating))
                        count++
                    }
                }

                Log.d("MOVIELIST", "Read $count movies from file")
                bufferedReader.close()
                movieAdapter.notifyDataSetChanged()
            } else {
                // Add fallback data if file doesn't exist
                Log.d("MOVIELIST", "File doesn't exist, adding sample data")
                movieList.add(Movie("The Godfather", "1972", "Crime", "9.2"))
                movieList.add(Movie("The Dark Knight", "2008", "Action", "9.0"))
                movieList.add(Movie("The Matrix", "1999", "Science Fiction", "8.7"))
                movieAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e("MOVIELIST", "Error reading file: ${e.message}")
            e.printStackTrace()

            // Add fallback data if there's an error
            movieList.add(Movie("The Godfather", "1972", "Crime", "9.2"))
            movieList.add(Movie("The Dark Knight", "2008", "Action", "9.0"))
            movieList.add(Movie("The Matrix", "1999", "Science Fiction", "8.7"))
            movieAdapter.notifyDataSetChanged()
        }
    }

    // Function to write to the file
    private fun writeFile() {
        try {
            val file = File(myPlace, "MOVIELIST.csv")
            val fileOutputStream = FileOutputStream(file)

            var count = 0
            for (movie in movieList) {
                val line = "${movie.title},${movie.year},${movie.genre},${movie.rating}\n"
                fileOutputStream.write(line.toByteArray())
                count++
            }

            fileOutputStream.close()
            Log.d("MOVIELIST", "Wrote $count movies to file")
        } catch (e: Exception) {
            Log.e("MOVIELIST", "Error writing file: ${e.message}")
            e.printStackTrace()
        }
    }

    // Function called when SAVE LIST button is clicked
    private fun saveList() {
        Log.d("MOVIELIST", "saveList() called")
        writeFile()
    }

    // Function to launch second activity
    private fun startSecond() {
        Log.d("MOVIELIST", "startSecond() called")
        val intent = Intent(this, AddMovieActivity::class.java)
        startForResult.launch(intent)
    }

    // ActivityResultLauncher for getting data back from second activity
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MOVIELIST", "Activity result received, code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val title = data.getStringExtra("TITLE") ?: ""
                val year = data.getStringExtra("YEAR") ?: ""
                val genre = data.getStringExtra("GENRE") ?: ""
                val rating = data.getStringExtra("RATING") ?: ""

                Log.d("MOVIELIST", "New movie data: $title, $year, $genre, $rating")

                val newMovie = Movie(title, year, genre, rating)
                movieList.add(newMovie)
                movieAdapter.notifyDataSetChanged()
                Log.d("MOVIELIST", "Movie added, list size: ${movieList.size}")
            } else {
                Log.d("MOVIELIST", "Result data is null")
            }
        } else {
            Log.d("MOVIELIST", "Activity result not OK")
        }
    }
}