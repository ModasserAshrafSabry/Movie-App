package com.example.movieapp.ui.navigation
// Screen: Sealed class لتمثيل كل شاشة في التطبيق كمسار (Route)
// استخدام Sealed Class يجعل التنقّل آمن (type-safe)
// ويمنع الأخطاء الناتجة عن كتابة Routes كسلسلة نصية عشوائية
sealed class Screen(val route: String) {
    // الشاشة الرئيسية Home
    // route → "home"
    object Home : Screen("home")
    // شاشة تفاصيل الفيلم Movie Details
    // نستخدم {movieJson} لتمرير بيانات الفيلم كنص مشفّر
    object Details : Screen("details/{movieJson}")
    // شاشة الـ Watchlist
    object Watchlist : Screen("watchlist")
    // شاشة البحث Search
    object Search : Screen("search")
    // شاشة تفاصيل المشاهير Celebrity Details
    // نمرر بيانات الشخص كنص JSON مشفّر
    object CelebrityDetails : Screen("celebrityDetails/{celebrityJson}")
    // شاشة البروفايل Profile — هتظهر في الـ Bottom Nav
    object Profile : Screen("profile")
}
