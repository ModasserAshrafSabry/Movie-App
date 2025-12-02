package com.example.movieapp.network

// 🎯 يحتوي على روابط وثوابت أساسية للـ TMDB API
object ApiConstants {

    // 🌍 عنوان الأساس لكل الطلبات
    const val BASE_URL = "https://api.themoviedb.org/3/"

    // 🖼️ عنوان الأساس لتحميل الصور من TMDB
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    // 🔹 روابط جاهزة (Endpoints)
    const val TRENDING_MOVIES = "trending/movie/week"       // الأفلام التريندينج
    const val TRENDING_CELEBRITIES = "trending/person/week" // المشاهير التريندينج
}
