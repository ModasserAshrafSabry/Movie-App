package com.example.movieapp.ui.watchlist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.movieapp.data.local.MovieEntity
import org.junit.Rule
import org.junit.Test

class WatchlistScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun watchlistScreen_showsEmptyMessage_whenListIsEmpty() {
        composeTestRule.setContent {
            WatchlistScreen(
                watchlist = emptyList(),
                onBackClick = {},
                onMovieClick = {},
                onRemoveClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("emptyMessage")
            .assertIsDisplayed()
    }

    @Test
    fun watchlistScreen_showsMovieItem_whenListIsNotEmpty() {
        val movie = MovieEntity(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            voteAverage = 8.8,
            overview = "A mind-bending thriller"
        )

        composeTestRule.setContent {
            WatchlistScreen(
                watchlist = listOf(movie),
                onBackClick = {},
                onMovieClick = {},
                onRemoveClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("movieItem_1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("movieRow_1")
            .assertIsDisplayed()
    }


    @Test
    fun watchlistScreen_backButton_isClickable() {
        var backClicked = false

        composeTestRule.setContent {
            WatchlistScreen(
                watchlist = emptyList(),
                onBackClick = { backClicked = true },
                onMovieClick = {},
                onRemoveClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag("backButton")
            .performClick()

        assert(backClicked)
    }


    @Test
    fun watchlistScreen_removeButton_isClickable() {
        var removedMovie: MovieEntity? = null
        val movie = MovieEntity(
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            voteAverage = 8.8,
            overview = "A mind-bending thriller"
        )

        composeTestRule.setContent {
            WatchlistScreen(
                watchlist = listOf(movie),
                onBackClick = {},
                onMovieClick = {},
                onRemoveClick = { movie -> removedMovie = movie }
            )
        }

        composeTestRule
            .onNodeWithTag("removeButton_1")
            .performClick()

        assert(removedMovie == movie)
    }
}
