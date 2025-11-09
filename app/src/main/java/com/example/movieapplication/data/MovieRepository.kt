package com.example.movieapp.data

import android.util.Log
import com.example.movieapp.model.CelebrityResponse
import com.example.movieapp.model.MovieResponse
import com.example.movieapp.network.ApiService
import com.example.movieapp.network.RetrofitInstance
import com.example.movieapp.BuildConfig

// 🎯 مسؤول عن جلب البيانات من API (الأفلام والمشاهير)
class MovieRepository {

    // ✅ إنشاء instance من ApiService اللي فيه دوال الاتصال بالسيرفر
    private val apiService: ApiService = RetrofitInstance.api

    // 🧩 دالة تجيب الأفلام التريندينج من السيرفر
    suspend fun getTrendingMovies(): MovieResponse? {
        return try {
            // 🛰️ استدعاء دالة من ApiService لجيب بيانات الأفلام
            val response = apiService.getTrendingMovies(BuildConfig.TMDB_API_KEY)
            response

        } catch (e: Exception) {
            // ❌ في حالة حدوث خطأ
            Log.e("MoviesCheck", "Error fetching movies: ${e.message}")
            null
        }
    }

    // 🧩 دالة تجيب المشاهير التريندينج
    suspend fun getTrendingCelebrities(): CelebrityResponse? {
        return try {
            val response = apiService.getTrendingCelebrities(BuildConfig.TMDB_API_KEY)

            response

        } catch (e: Exception) {
            Log.e("MoviesCheck", "Error fetching celebrities: ${e.message}")
            null
        }
    }

    // 🧩 دالة بحث عن أفلام
    suspend fun searchMovies(query: String): MovieResponse {
        return apiService.searchMovies(BuildConfig.TMDB_API_KEY, query)
    }
    suspend fun searchCelebrities(query: String): CelebrityResponse {
        return apiService.searchCelebrities(BuildConfig.TMDB_API_KEY, query)
    }
}
