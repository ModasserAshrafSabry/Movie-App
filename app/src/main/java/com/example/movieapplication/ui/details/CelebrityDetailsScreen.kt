package com.example.movieapp.ui.celebrity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.model.Celebrity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelebrityDetailsScreen(
    celebrity: Celebrity,
    onBackClick: () -> Unit
) {
    val profileUrl = celebrity.profilePath?.let {
        "https://image.tmdb.org/t/p/w500$it"
    } ?: "https://via.placeholder.com/300x300?text=No+Image"

    // 🧱 تصميم الشاشة باستخدام Scaffold (مع TopAppBar)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(celebrity.name ?: "Unknown", color = Color.White) },
                navigationIcon = {
                    // 🔙 زر الرجوع
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->

        // 🎨 المحتوى الرئيسي للشاشة
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 🖼️ صورة المشهور
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = celebrity.name ?: "Celebrity image",
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 👤 الاسم
            Text(
                text = celebrity.name ?: "Unknown",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // 🎭 الدور أو المجال
            Text(
                text = "Known for: ${celebrity.role ?: "N/A"}",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 📝 وصف بسيط
            Text(
                text = "This actor is one of the trending celebrities today!",
                color = Color.LightGray,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 22.sp
            )
        }
    }
}
