package com.example.movieapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


// -------------------------------------------------------------
// Data class لعنصر واحد في الـ Bottom Navigation
// يحتوي على:
// icon  → Composable لعرض الأيكون
// label → اسم التاب
// route → المسار الخاص بالتاب
// -------------------------------------------------------------
data class BottomNavItem(
    val icon: @Composable () -> Unit,
    val label: String,
    val route: String
)


// -------------------------------------------------------------
// Composable: BottomNavigationBar
// شريط تنقل عائم (Floating) ثابت يظهر في كل الشاشات
// currentRoute → لمعرفة الشاشة النشطة
// onItemClick  → دالة التنقل عند الضغط على التاب
// -------------------------------------------------------------
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {

    // قائمة الـ Tabs
    val items = listOf(
        BottomNavItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentRoute == Screen.Home.route) Color.White else Color.Gray
                )
            },
            label = "Home",
            route = Screen.Home.route
        ),
        BottomNavItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Wishlist",
                    tint = if (currentRoute == Screen.Watchlist.route) Color.White else Color.Gray
                )
            },
            label = "Wishlist",
            route = Screen.Watchlist.route
        ),
        BottomNavItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (currentRoute == Screen.Profile.route) Color.White else Color.Gray
                )
            },
            label = "Profile",
            route = Screen.Profile.route
        )
    )



    // ---------------------------------------------------------
    // تصميم الـ Floating Bottom Navigation Bar
    // ---------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp) // رفع البار للأعلى ليظهر عائم
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(50.dp)) // الشكل الدائري مثل الصورة
                .background(Color(0xFF1E1E1E))  // اللون الداكن
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val isSelected = currentRoute == item.route

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onItemClick(item.route) }
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // الأيقونة (الصحيحة)
                    item.icon()

                    Text(
                        text = item.label,
                        color = if (isSelected) Color.White else Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
