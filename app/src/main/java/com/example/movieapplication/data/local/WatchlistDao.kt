package com.example.movieapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 🎯 DAO لإدارة قائمة الـ Watchlist (الأفلام المحفوظة)
@Dao
interface WatchlistDao {

    // 📤 ترجع كل الأفلام في الـ watchlist
    @Query("SELECT * FROM watchlist")
    fun getAllMovies(): Flow<List<MovieEntity>>

    // ➕ تضيف فيلم إلى الـ watchlist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: MovieEntity)

    // ❌ تحذف فيلم من الـ watchlist
    @Delete
    suspend fun removeMovie(movie: MovieEntity)

    // 🔍 تتحقق إذا كان الفيلم موجود أصلاً في الـ watchlist
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    suspend fun isMovieInWatchlist(movieId: Int): Boolean
}

