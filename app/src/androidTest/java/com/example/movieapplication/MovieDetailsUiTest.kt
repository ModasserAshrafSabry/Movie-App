package com.example.movieapplication.ui.details

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.movieapp.model.Movie
import com.example.movieapp.ui.details.MovieDetailsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailsUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleMovie = Movie(
        id = 1,
        title = "Test Movie",
        posterPath = null,
        voteAverage = 8.5,
        overview = "This is a test overview.",
        backdropPath = null,
        releaseDate = "2025-11-26"
    )

    @Test
    fun overview_isDisplayed() {
        val navController = TestNavHostController(composeTestRule.activity)

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = navController,
                onBackClick = {}
            )
        }

        // تحقق أن النص الخاص بالoverview معروض
        composeTestRule.onNodeWithText("This is a test overview.").assertIsDisplayed()
    }

    @Test
    fun rating_isDisplayed() {
        val navController = TestNavHostController(composeTestRule.activity)

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = navController,
                onBackClick = {}
            )
        }

        // تحقق أن تقييم الفيلم معروض
        composeTestRule.onNodeWithText("8.5/10").assertIsDisplayed()
    }

    @Test
    fun addToPlaylistButton_works() {
        var playlistClicked = false

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = TestNavHostController(composeTestRule.activity),
                onBackClick = {},
                onAddToPlaylist = { playlistClicked = true } // override for test
            )
        }

        val addButton = composeTestRule.onNodeWithText("+ Add to playlist")
        addButton.assertIsDisplayed()
        addButton.performClick()

        // Check that the callback was called
        assert(playlistClicked) { "Add to playlist button was not clicked" }
    }

    @Test
    fun playTrailerButton_works() {
        var trailerClicked = false

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = TestNavHostController(composeTestRule.activity),
                onBackClick = {},
                onPlayTrailer = { trailerClicked = true } // override for test
            )
        }

        composeTestRule.onNodeWithContentDescription("Play Trailer").performClick()

        assert(trailerClicked) { "Trailer button was not clicked" }
    }
}
