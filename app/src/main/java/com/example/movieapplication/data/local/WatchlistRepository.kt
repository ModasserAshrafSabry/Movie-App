package com.example.movieapp.data.local

import kotlinx.coroutines.flow.Flow

class WatchlistRepository(private val dao: WatchlistDao) {

    fun getAllMovies(): Flow<List<MovieEntity>> = dao.getAllMovies()

    suspend fun addMovie(movie: MovieEntity) {
        dao.addMovie(movie)
    }

    suspend fun removeMovie(movie: MovieEntity) {
        dao.removeMovie(movie)
    }

    suspend fun isMovieInWatchlist(movieId: Int): Boolean {
        return dao.isMovieInWatchlist(movieId)
    }
}

