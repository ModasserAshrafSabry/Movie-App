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
        posterPath = "/posterTest.jpg",
        voteAverage = 8.5,
        overview = "This is a test overview.",
        backdropPath = null,
        releaseDate = "2025-11-26"
    )

    // -----------------------------
    // 1) Overview should be visible
    // -----------------------------
    @Test
    fun overview_isDisplayed() {
        val navController = TestNavHostController(composeTestRule.activity)

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = navController
            )
        }

        composeTestRule.onNodeWithText("This is a test overview.").assertIsDisplayed()
    }

    // -----------------------------
    // 2) Rating should be visible
    // -----------------------------
    @Test
    fun rating_isDisplayed() {
        val navController = TestNavHostController(composeTestRule.activity)

        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = navController
            )
        }

        composeTestRule.onNodeWithText("8.5/10").assertIsDisplayed()
    }

    // -----------------------------
    // 3) Poster should be displayed
    // -----------------------------
    @Test
    fun poster_isDisplayed() {
        composeTestRule.setContent {
            MovieDetailsScreen(
                movie = sampleMovie,
                navController = TestNavHostController(composeTestRule.activity),
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Movie Poster")
            .assertIsDisplayed()
    }
}
