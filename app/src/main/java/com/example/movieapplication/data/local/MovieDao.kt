package com.example.movieapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 🎯 واجهة DAO لإدارة جدول watchlist
@Dao
interface MovieDao {

    // 📤 تجيب كل الأفلام الموجودة
    @Query("SELECT * FROM watchlist")
    fun getAllMovies(): Flow<List<MovieEntity>>

    // ➕ تضيف فيلم جديد
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movie: MovieEntity)

    // ❌ تحذف فيلم بالكامل
    @Delete
    suspend fun removeMovie(movie: MovieEntity)

    // ❌ تحذف فيلم باستخدام ID فقط
    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun removeById(movieId: Int)
}
