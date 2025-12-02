package com.example.movieapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 🎯 قاعدة بيانات خاصة بالـ Watchlist
@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class WatchlistDatabase : RoomDatabase() {

    // 🔗 DAO الخاص بالـ watchlist
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: WatchlistDatabase? = null

        // 🧱 Singleton: إنشاء قاعدة البيانات مرة واحدة فقط
        fun getDatabase(context: Context): WatchlistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WatchlistDatabase::class.java,
                    "watchlist_db" // 📦 اسم قاعدة البيانات
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

