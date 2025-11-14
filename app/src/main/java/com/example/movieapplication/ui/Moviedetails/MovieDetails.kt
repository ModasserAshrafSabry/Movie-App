package com.example.movieapplication.ui.Moviedetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.movieapp.model.MovieDetails
import com.example.movieapp.ui.details.MovieDetailsScreen
import com.example.movieapplication.ui.Moviedetails.ui.theme.MovieApplicationTheme

class MovieDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movie = intent.getSerializableExtra("movie") as? MovieDetails
        if (movie == null) {
            finish()
            return
        }

        setContent {
            MovieApplicationTheme {
                MovieDetailsScreen(
                    movie = movie,
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        fun navigate(context: Context, movie: MovieDetails) {
            val intent = Intent(context, MovieDetails::class.java)
            intent.putExtra("movie", movie)
            context.startActivity(intent)
        }
    }
}
