package com.example.movieapp.ui.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movieapplication.ui.viewmodel.SharedProfileViewModel
import com.example.movieapplication.ui.watchlist.CelebModel
import com.example.movieapplication.ui.watchlist.GenreModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    sharedViewModel: SharedProfileViewModel,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCelebrity: (String) -> Unit = {},
    onNavigateToGenre: (String) -> Unit = {}
) {
    val favoriteCelebs by sharedViewModel.favoriteCelebs.collectAsState()
    val favoriteGenres by sharedViewModel.favoriteGenres.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile Placeholder", modifier = Modifier.size(40.dp), tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Username", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("email@example.com", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Favorite Genres
            Text("Favorite Genres", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favoriteGenres) { genre ->
                    Surface(
                        onClick = { onNavigateToGenre(genre.name) },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1E1E)
                    ) {
                        Text(genre.name, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Favorite Celebrities
            Text("Favorite Celebrities", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(favoriteCelebs) { celeb ->
                    Card(
                        onClick = { onNavigateToCelebrity(celeb.name) },
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Celebrity Placeholder", modifier = Modifier.size(24.dp), tint = Color.White)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(celeb.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 1)
                            Text(celeb.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
