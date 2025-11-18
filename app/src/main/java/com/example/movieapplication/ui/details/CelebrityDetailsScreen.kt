package com.example.movieapp.ui.celebrity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.model.Celebrity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelebrityDetailsScreen(
    basicCelebrity: Celebrity,
    repository: MovieRepository,
    onBackClick: () -> Unit
) {
    var currentCelebrity by remember(basicCelebrity.id) { mutableStateOf(basicCelebrity) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableStateOf(0) }

    // Main data fetching effect
    LaunchedEffect(basicCelebrity.id, retryTrigger) {
        isLoading = true
        errorMessage = null

        try {
            val details = repository.getCelebrityDetails(basicCelebrity.id)
            val images = repository.getCelebrityImages(basicCelebrity.id)

            if (details != null) {
                val updatedCelebrity = Celebrity(
                    id = basicCelebrity.id,
                    name = details.name ?: basicCelebrity.name,
                    role = details.role,
                    profilePath = basicCelebrity.profilePath ?: details.profilePath,
                    birthday = details.birthday,
                    placeOfBirth = details.placeOfBirth,
                    biography = details.biography,
                    profileImagePaths = images
                )
                currentCelebrity = updatedCelebrity
            } else {
                errorMessage = "Failed to load details"
            }
        } catch (e: Exception) {
            errorMessage = "Network error: ${e.message}"
            Log.e("CELEBRITY_DEBUG", "ERROR: ${e.message}")
        } finally {
            isLoading = false
            Log.d("CELEBRITY_DEBUG", "EFFECT: Completed")
        }
    }

    val imageUrl = { path: String?, size: String ->
        if (!path.isNullOrBlank()) {
            "https://image.tmdb.org/t/p/$size$path"
        } else {
            "https://via.placeholder.com/300x450?text=No+Image"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Celebrity Details",
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Black.copy(0.95f))
            )
        },
        containerColor = Color.Black
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Loading...", color = Color.White)
                }
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(errorMessage ?: "Error", color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        AsyncImage(
                            model = imageUrl(currentCelebrity.profilePath, "w500"),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(120.dp, 160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentCelebrity.name,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            currentCelebrity.birthday?.let { birthday ->
                                if (birthday.isNotBlank() && birthday != "null") {
                                    Text("Born: $birthday", color = Color.LightGray, fontSize = 14.sp)
                                    Spacer(Modifier.height(4.dp))
                                }
                            }

                            currentCelebrity.placeOfBirth?.let { place ->
                                if (place.isNotBlank() && place != "null") {
                                    Text("From: $place", color = Color.LightGray, fontSize = 14.sp)
                                }
                            }

                            currentCelebrity.role?.let { role ->
                                if (role.isNotBlank() && role != "null") {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Known for: $role",
                                        color = Color.LightGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(Color(0xFFd8fd33)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Add to Favourites", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(20.dp))

                    currentCelebrity.biography?.let { bio ->
                        if (bio.isNotBlank() && bio != "null") {
                            SectionHeader("Biography")
                            Text(
                                text = bio,
                                color = Color.LightGray,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            HorizontalDivider(
                                color = Color.DarkGray.copy(0.5f),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }

                    if (currentCelebrity.profileImagePaths.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("|", color = Color(0xFFd8fd33), fontSize = 27.sp)
                            Text(
                                "Photos (${currentCelebrity.profileImagePaths.size})",
                                color = Color.White,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        val photosToShow = currentCelebrity.profileImagePaths.take(10)

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.height(180.dp)
                        ) {
                            items(photosToShow) { path ->
                                AsyncImage(
                                    model = imageUrl(path, "w300"),
                                    contentDescription = "Celebrity photo",
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text("See all", color = Color(0xFFE50914), fontSize = 14.sp)
    }
}
