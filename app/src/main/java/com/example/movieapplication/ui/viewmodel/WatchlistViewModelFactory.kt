package com.example.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.WatchlistRepository

// 🏭 Factory لإنشاء WatchlistViewModel مع تمرير الـ Repository والـ Database إليه
class WatchlistViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // 📦 دالة لإنشاء ViewModel جديد
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 👇 نتحقق إن نوع الـ ViewModel المطلوب هو WatchlistViewModel
        if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {

            // 🧱 إنشاء قاعدة بيانات Room بإسم "movie_db"
            val database = Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                "movie_db"
            ).build()

            // 🗂️ إنشاء Repository للتعامل مع قاعدة البيانات
            val repository = WatchlistRepository(database.watchlistDao())

            // 🎬 إنشاء نسخة من WatchlistViewModel وتمرير الـ Repository لها
            @Suppress("UNCHECKED_CAST")
            return WatchlistViewModel(repository) as T
        }

        // ⚠️ في حال تم طلب نوع ViewModel غير معروف
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
