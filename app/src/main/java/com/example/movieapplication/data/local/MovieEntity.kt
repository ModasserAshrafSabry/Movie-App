package com.example.movieapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieapp.model.Celebrity
import com.google.gson.annotations.SerializedName

// 🎯 تعريف الجدول في قاعدة البيانات
@Entity(tableName = "watchlist")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double?,
    val overview: String?,

)

