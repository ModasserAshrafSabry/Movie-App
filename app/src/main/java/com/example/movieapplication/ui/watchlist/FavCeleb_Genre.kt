package com.example.movieapplication.ui.watchlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.ui.profile.ProfileViewModel
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapplication.ui.Login.LoginActivity

data class GenreModel(val id: Int, val name: String)

class FavCeleb_Genre : ComponentActivity() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val genreList = listOf(
            GenreModel(28, "Action"), GenreModel(12, "Adventure"),
            GenreModel(16, "Animation"), GenreModel(35, "Comedy"),
            GenreModel(80, "Crime"), GenreModel(18, "Drama"),
            GenreModel(27, "Horror"), GenreModel(10749, "Romance"),
            GenreModel(53, "Thriller"), GenreModel(37, "Western")
        )

        setContent {
            MovieAppTheme {
                Log.d("FavCeleb_Genre", "Setting content for GenreSelectionScreen")
                GenreSelectionScreen(
                    viewModel = profileViewModel,
                    genres = genreList,
                    onDoneClick = {
                        Log.d("FavCeleb_Genre", "Done clicked - Starting LoginActivity")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelectionScreen(
    viewModel: ProfileViewModel,
    genres: List<GenreModel>,
    onDoneClick: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedGenres = remember { mutableStateListOf<String>() }

    LaunchedEffect(profileState.favoriteGenres) {
        selectedGenres.clear()
        selectedGenres.addAll(profileState.favoriteGenres)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Red)
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Choose Favorite Genres", color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
                )
            },
            containerColor = Color.Black
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Select your favorite genres",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("You can change this later", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                items(genres) { genre ->
                    val isSelected = selectedGenres.contains(genre.name)

                    GenreItem(
                        genre = genre,
                        isSelected = isSelected,
                        onToggleSelected = {
                            if (isSelected) {
                                selectedGenres.remove(genre.name)
                                viewModel.removeFavoriteGenre(genre.name)
                            } else {
                                selectedGenres.add(genre.name)
                                viewModel.saveFavoriteGenre(genre.name)
                            }
                        }
                    )

                    Divider(color = Color.DarkGray, thickness = 1.dp)
                }

                item {
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onDoneClick,
                        enabled = selectedGenres.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            disabledContainerColor = Color.Red.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            "Done",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}


@Composable
fun GenreItem(genre: GenreModel, isSelected: Boolean, onToggleSelected: () -> Unit) {
    Log.d("GenreItem", "Composing GenreItem → ${genre.name}, Selected: $isSelected")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = genre.name,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                Log.d("GenreItem", "Icon clicked: ${genre.name}")
                onToggleSelected()
            }
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isSelected) "Remove from Favorites" else "Add to Favorites",
                tint = if (isSelected) Color.Red else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
