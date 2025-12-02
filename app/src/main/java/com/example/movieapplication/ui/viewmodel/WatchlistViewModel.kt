package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.data.local.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// 🎬 ViewModel خاص بقائمة المشاهدة (Watchlist)
class WatchlistViewModel(private val repository: WatchlistRepository) : ViewModel() {

    // 🔹 تدفق (Flow) يحتوي على جميع الأفلام الموجودة في قاعدة البيانات
    val allMovies: Flow<List<MovieEntity>> = repository.getAllMovies()

    // ➕ دالة لإضافة فيلم جديد إلى قاعدة البيانات
    fun addMovie(movie: MovieEntity) = viewModelScope.launch {
        // نستخدم Coroutine لتشغيل العملية في الخلفية بدون تجميد الواجهة
        repository.addMovie(movie)
    }

    // ❌ دالة لحذف فيلم من قاعدة البيانات
    fun removeMovie(movie: MovieEntity) = viewModelScope.launch {
        repository.removeMovie(movie)
    }
}
