package com.example.movieapp // 📦 اسم الباكدج اللي بيتبعها الملف

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.WatchlistRepository
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.home.HomeViewModelFactory
import com.example.movieapp.ui.navigation.AppNavigation
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapp.viewmodel.WatchlistViewModel
import com.example.movieapp.viewmodel.WatchlistViewModelFactory

// 🎬 MainActivity هي أول Activity بتبدأ لما التطبيق يشتغل
class MainActivity : ComponentActivity() {

    // ✅ إنشاء HomeViewModel يدويًا باستخدام Factory
    private val homeViewModel: HomeViewModel by lazy {
        // 🔹 إنشاء repository خاص بالأفلام للتعامل مع API
        val movieRepository = MovieRepository()

        // 🔹 إنشاء قاعدة البيانات المحلية (Room Database)
        val database = AppDatabase.getDatabase(application)

        // 🔹 إنشاء repository خاص بقائمة المشاهدة (Watchlist)
        val watchlistRepository = WatchlistRepository(database.watchlistDao())

        // 🔹 استخدام ViewModelProvider لإنشاء HomeViewModel بتمرير الـ repositories إليه
        ViewModelProvider(
            this,
            HomeViewModelFactory(movieRepository, watchlistRepository)
        )[HomeViewModel::class.java]
    }

    // ✅ إنشاء WatchlistViewModel (لو حابب تستخدمه لاحقًا)
    private val watchlistViewModel: WatchlistViewModel by lazy {
        // 🔹 نفس الفكرة، لكن هنا بنستخدم Factory خاص بالـ WatchlistViewModel
        ViewModelProvider(
            this,
            WatchlistViewModelFactory(application)
        )[WatchlistViewModel::class.java]
    }

    // 🧩 دالة onCreate بتتنفذ أول ما الـ Activity تشتغل
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 تمكين ميزة Edge-to-Edge لتخلي الواجهة تغطي الشاشة بالكامل (من الحافة للحافة)
        enableEdgeToEdge()

        // 🖥️ setContent هي اللي بتعرض واجهة المستخدم بتاعة Compose
        setContent {
            // 🎨 تطبيق الثيم العام بتاع التطبيق
            MovieAppTheme {
                // 🧱 Surface هي حاوية رئيسية بلون خلفية معين
                Surface(modifier = Modifier.fillMaxSize()) {

                    // 🚀 استدعاء AppNavigation لإدارة التنقل بين الشاشات
                    // 🔹 بنمرر الـ homeViewModel علشان يكون متاح في كل الشاشات
                    AppNavigation(viewModel = homeViewModel)
                }
            }
        }
    }
}
