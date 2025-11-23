package com.example.movieapp.ui.celebrity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.model.Celebrity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

val db = FirebaseFirestore.getInstance()
val auth = FirebaseAuth.getInstance()

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
    var isFavorite by remember { mutableStateOf(false) }
    var isLoadingFavorite by remember { mutableStateOf(true) }

    // Check if celebrity is in favorites
    LaunchedEffect(currentCelebrity.id) {
        checkIfFavorite(currentCelebrity.id) { favorite ->
            isFavorite = favorite
            isLoadingFavorite = false
        }
    }

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

                // Check favorite status again when celebrity data is loaded
                checkIfFavorite(updatedCelebrity.id) { favorite ->
                    isFavorite = favorite
                    isLoadingFavorite = false
                }
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
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 18.dp)
                        ,horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        AsyncImage(
                            model = imageUrl(currentCelebrity.profilePath, "w500"),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(120.dp, 160.dp),
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
                                    Text(
                                        "Born: $birthday",
                                        color = Color.LightGray,
                                        fontSize = 14.sp
                                    )
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

                    // Updated Favorite Button
                    Button(
                        onClick = {
                            if (isFavorite) {
                                removeFromFavorites(currentCelebrity)
                                isFavorite = false
                            } else {
                                addToFavorites(currentCelebrity)
                                isFavorite = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFavorite) Color(0xFFDE6A6F) else Color(0xFFd8fd33)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .height(48.dp),
                        enabled = !isLoadingFavorite,
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoadingFavorite) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.Add,
                                    contentDescription = "Favorite",
                                    tint = Color.Black
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))



                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1c1c1c))
                    ) {
                        currentCelebrity.biography?.let { bio ->
                            if (bio.isNotBlank() && bio != "null") {
                                val maxLinesCollapsed = 4
                                var expanded by remember { mutableStateOf(false) }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(start = 18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("|", color = Color(0xFFd8fd33), fontSize = 27.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "Biography",
                                            color = Color.White,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = bio.trim(),
                                        color = Color.LightGray,
                                        fontSize = 15.sp,
                                        lineHeight = 24.sp,
                                        maxLines = if (expanded) Int.MAX_VALUE else maxLinesCollapsed,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(horizontal = 19.dp)
                                            .clickable { expanded = !expanded }
                                    )

                                    if (bio.lines().size > maxLinesCollapsed || bio.length > 200) {
                                        Text(
                                            text = if (expanded) "Show less" else "Read more",
                                            color = Color(0xFFd8fd33),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            modifier = Modifier
                                                .padding(start = 19.dp, top = 8.dp, bottom = 20.dp)
                                                .clickable { expanded = !expanded }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }
                    }



                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1c1c1c))
                    ) {
                        if (currentCelebrity.profileImagePaths.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("|", color = Color(0xFFd8fd33), fontSize = 27.sp)
                                    Spacer(Modifier.width(8.dp))
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
                                    contentPadding = PaddingValues(horizontal = 18.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .padding( bottom = 25.dp)

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
                        }
                    }

                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }
}

private fun checkIfFavorite(celebrityId: Int, onResult: (Boolean) -> Unit) {
    val userId = auth.currentUser?.uid ?: run {
        onResult(false)
        return
    }

    db.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val favorites =
                    document.get("favoriteCelebrities") as? List<Map<String, String>> ?: emptyList()
                val isFavorite = favorites.any { it["id"] == celebrityId.toString() }
                onResult(isFavorite)
            } else {
                onResult(false)
            }
        }
        .addOnFailureListener {
            onResult(false)
        }
}

private fun addToFavorites(celebrity: Celebrity) {
    val userId = auth.currentUser?.uid ?: return

    val imagePath = celebrity.profilePath?.let { path ->
        if (path.startsWith("http")) {
            path.substringAfter("/w200")
        } else {
            path
        }
    } ?: ""

    val celebrityData = hashMapOf<String, String>(
        "id" to celebrity.id.toString(),
        "name" to celebrity.name,
        "role" to (celebrity.role ?: "Actor"),
        "imageUrl" to imagePath
    )

    db.collection("users").document(userId)
        .set(
            mapOf("favoriteCelebrities" to FieldValue.arrayUnion(celebrityData)),
            SetOptions.merge()
        )
        .addOnSuccessListener {
            Log.d("FAVORITES", "Celebrity added to favorites")
        }
        .addOnFailureListener { e ->
            Log.e("FAVORITES", "Error adding to favorites: ${e.message}")
        }
}

private fun removeFromFavorites(celebrity: Celebrity) {
    val userId = auth.currentUser?.uid ?: return


    val celebrityData = hashMapOf<String, String>(
        "id" to celebrity.id.toString(),
        "name" to celebrity.name,
        "role" to (celebrity.role ?: "Actor"),
        "imageUrl" to (celebrity.profilePath ?: "")
    )

    db.collection("users").document(userId)
        .update("favoriteCelebrities", FieldValue.arrayRemove(celebrityData))
        .addOnSuccessListener {
            Log.d("FAVORITES", "Celebrity removed from favorites")
        }
        .addOnFailureListener { e ->
            Log.e("FAVORITES", "Error removing from favorites: ${e.message}")
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