package com.example.movieapplication.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun CelebrityListScreen(
    celebrities: List<Celebrity>,
    onCelebrityClick: (Celebrity) -> Unit,
    onBackClick: () -> Unit,
    onToggleFavorite: (Celebrity) -> Unit = {},
    favoriteIds: Set<Int> = emptySet()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending Celebrities", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(celebrities) { celebrity ->
                CelebrityListItem(
                    celebrity = celebrity,
                    isFavorite = favoriteIds.contains(celebrity.id),
                    onClick = { onCelebrityClick(celebrity) },
                    onToggleFavorite = { onToggleFavorite(celebrity) }
                )
                Divider(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }
            item {
                Spacer(modifier = Modifier.height(95.dp))
            }
        }

    }
}

@Composable
fun CelebrityListItem(
    celebrity: Celebrity,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit = {}
) {
    val profileUrl = celebrity.profilePath?.let {
        if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
        else "https://image.tmdb.org/t/p/w500/$it"
    } ?: "https://via.placeholder.com/100x100?text=No+Image"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = celebrity.name ?: "Celebrity",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Celebrity Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = celebrity.name ?: "Unknown",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Role/Known For
            Text(
                text = celebrity.role ?: "Actor",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}