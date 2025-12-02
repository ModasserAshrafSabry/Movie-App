package com.example.movieapp.data.local

import kotlinx.coroutines.flow.Flow

// 🎯 Repository للتعامل مع الـ Watchlist عبر DAO
class WatchlistRepository(private val dao: WatchlistDao) {

    // 📤 تجيب كل الأفلام
    fun getAllMovies(): Flow<List<MovieEntity>> = dao.getAllMovies()

    // ➕ تضيف فيلم
    suspend fun addMovie(movie: MovieEntity) {
        dao.addMovie(movie)
    }

    // ❌ تحذف فيلم
    suspend fun removeMovie(movie: MovieEntity) {
        dao.removeMovie(movie)
    }

    // 🔍 تتحقق إذا كان الفيلم موجود
    suspend fun isMovieInWatchlist(movieId: Int): Boolean {
        return dao.isMovieInWatchlist(movieId)
    }
}

