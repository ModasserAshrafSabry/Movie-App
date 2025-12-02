package com.example.movieapp.ui.home

// 🧩 استيراد فئة ViewModel (اللي بنستخدمها لإدارة البيانات المنفصلة عن الـ UI)
import androidx.lifecycle.ViewModel
// 🧩 استيراد ViewModelProvider (اللي بنستخدمه لإنشاء الـ ViewModel بشكل ديناميكي)
import androidx.lifecycle.ViewModelProvider
// 🧩 استيراد الريبو المسؤول عن جلب بيانات الأفلام من الـ API
import com.example.movieapp.data.MovieRepository
// 🧩 استيراد الريبو المسؤول عن إدارة قائمة المشاهدة (Watchlist)
import com.example.movieapp.data.local.WatchlistRepository

// 🏗️ تعريف كلاس "HomeViewModelFactory" اللي هي المصنع (Factory) لإنشاء ViewModel مخصص
class HomeViewModelFactory(
    // 🧱 تمرير نسخة من MovieRepository (علشان ViewModel يقدر يستخدمها)
    private val movieRepository: MovieRepository,
    // 🧱 تمرير نسخة من WatchlistRepository (علشان ViewModel يعرف يتعامل مع قاعدة البيانات المحلية)
    private val watchlistRepository: WatchlistRepository
) : ViewModelProvider.Factory {  // 🧩 هنا الكلاس بيورّث من ViewModelProvider.Factory

    // ⚙️ دالة create بتُستدعى لما النظام يحتاج ينشئ ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // ✅ التحقق هل الـ ViewModel المطلوب هو HomeViewModel
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // 🔄 لو صح، نرجع كائن جديد من HomeViewModel ونمررله الـ repositories
            return HomeViewModel(movieRepository, watchlistRepository) as T
        }
        // ⚠️ لو مش نفس النوع، نرمي خطأ علشان النظام يعرف إن النوع مش معروف
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

