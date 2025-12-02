package com.example.movieapp.model

// 🎯 الرد الكامل من API الخاص بالأفلام
data class MovieResponse(
    val results: List<Movie> // 📋 قائمة الأفلام
)
