package com.example.movieapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 🎯 تعريف قاعدة بيانات Room وارتباطها بجدول MovieEntity
@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class
AppDatabase : RoomDatabase() {

    abstract fun watchlistDao(): WatchlistDao
    abstract fun movieDao(): MovieDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 🧱 دالة Singleton: بتنشئ قاعدة البيانات مرة واحدة فقط
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_database" // 📦 اسم قاعدة البيانات
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}