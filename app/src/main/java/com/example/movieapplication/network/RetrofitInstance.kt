package com.example.movieapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 🎯 إنشاء Retrofit instance واحدة (Singleton) لاستخدامها في المشروع كله
object RetrofitInstance {

    // 🌍 عنوان الأساس (نفس اللي في ApiConstants)
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // 🧩 تهيئة Retrofit باستخدام Gson لتحويل JSON إلى Kotlin objects
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                         // 🔗 تحديد عنوان الأساس
            .addConverterFactory(GsonConverterFactory.create()) // 🧠 تحويل JSON إلى Data Classes
            .build()
            .create(ApiService::class.java)             // ✅ إنشاء ApiService الجاهز للاستخدام
    }
}
