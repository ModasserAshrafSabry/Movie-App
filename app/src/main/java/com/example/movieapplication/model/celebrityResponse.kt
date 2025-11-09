package com.example.movieapp.model

// 🎯 الكلاس ده بيمثل الرد الكامل من API الخاص بالمشاهير
data class CelebrityResponse(
    val results: List<Celebrity> // 📋 قائمة المشاهير الراجعة من السيرفر
)
