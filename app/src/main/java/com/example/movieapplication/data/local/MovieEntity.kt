package com.example.movieapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 🎯 تعريف الجدول في قاعدة البيانات
@Entity(tableName = "watchlist")
data class MovieEntity(
    @PrimaryKey val id: Int, // 🆔 المفتاح الرئيسي للفيلم
    val title: String,        // 🎬 اسم الفيلم
    val posterPath: String?,  // 🖼️ رابط صورة الفيلم
    val voteAverage: Double?, // ⭐ متوسط التقييم
    val overview: String?     // 📝 نبذة مختصرة
)
