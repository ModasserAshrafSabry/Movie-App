package com.example.movieapplication

import app.cash.turbine.test
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.data.local.WatchlistDao
import com.example.movieapp.data.local.WatchlistRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WatchlistRepositoryTest {

    private val dao = mockk<WatchlistDao>(relaxed = true)
    private val repository = WatchlistRepository(dao)

    @Test
    fun `getAllMovies returns flow from dao`() = runTest {
        val movies = listOf(
            MovieEntity(
                id = 1,
                title = "Test Movie",
                posterPath = "/test.jpg",
                overview = "Test overview",
                voteAverage = 7.5,
            )
        )

        coEvery { dao.getAllMovies() } returns flowOf(movies)

        repository.getAllMovies().test {
            assertEquals(movies, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `addMovie calls dao addMovie`() = runTest {
        val movie = MovieEntity(
            id = 550,
            title = "Fight Club",
            posterPath = "/fightclub.jpg",
            overview = "An insomniac...",
            voteAverage = 8.8,
        )

        repository.addMovie(movie)
        coVerify { dao.addMovie(movie) }
    }

    @Test
    fun `removeMovie calls dao removeMovie`() = runTest {
        val movie = MovieEntity(id = 550, title = "Fight Club", posterPath = null, overview = "", voteAverage = 0.0)
        repository.removeMovie(movie)
        coVerify { dao.removeMovie(movie) }
    }

    @Test
    fun `isMovieInWatchlist returns true when dao says yes`() = runTest {
        coEvery { dao.isMovieInWatchlist(550) } returns true
        assertTrue(repository.isMovieInWatchlist(550))
    }

    @Test
    fun `isMovieInWatchlist returns false when dao says no`() = runTest {
        coEvery { dao.isMovieInWatchlist(999) } returns false
        assertFalse(repository.isMovieInWatchlist(999))
    }
}