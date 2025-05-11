package com.example.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

//change

class MovieAdapter(private val movieList: ArrayList<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // ViewHolder class
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView
        var yearTextView: TextView
        var genreTextView: TextView
        var ratingTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.titleTextView)
            yearTextView = itemView.findViewById(R.id.yearTextView)
            genreTextView = itemView.findViewById(R.id.genreTextView)
            ratingTextView = itemView.findViewById(R.id.ratingTextView)
        }
    }

    // Create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(itemView)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = movieList[position]
        holder.titleTextView.text = currentMovie.title
        holder.yearTextView.text = "Year: ${currentMovie.year}"
        holder.genreTextView.text = "Genre: ${currentMovie.genre}"
        holder.ratingTextView.text = "Rating: ${currentMovie.rating}"
    }

    // Get item count
    override fun getItemCount(): Int {
        return movieList.size
    }

    // Function to remove item
    fun removeItem(position: Int) {
        movieList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Swipe to delete callback
    inner class SwipeToDeleteCallback :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) =
            if (viewHolder is MovieViewHolder) {
                makeMovementFlags(
                    ItemTouchHelper.ACTION_STATE_IDLE,
                    ItemTouchHelper.RIGHT
                ) or makeMovementFlags(
                    ItemTouchHelper.ACTION_STATE_SWIPE,
                    ItemTouchHelper.RIGHT
                )
            } else {
                0
            }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            removeItem(position)
        }
    }

    // added to enable swipe delete
    val swipeToDeleteCallback = SwipeToDeleteCallback()
}