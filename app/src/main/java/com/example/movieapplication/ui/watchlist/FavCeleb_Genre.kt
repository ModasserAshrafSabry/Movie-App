package com.example.movieapplication.ui.watchlist

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.R
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapplication.ui.viewmodel.SharedProfileViewModel
import androidx.navigation.NavController
import com.example.movieapplication.ui.Login.LoginActivity

// ---------------------- MODELS ----------------------
data class CelebModel(
    val id: String = "",
    val name: String,
    val imageRes: Int,
    val role: String
)

data class GenreModel(
    val id: Int,
    val name: String
)

// ---------------------- ACTIVITY ----------------------
class FavCeleb_Genre : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val mockCelebs = listOf(
            CelebModel("1", "Tom Cruise", R.drawable.shutter, "Actor"),
            CelebModel("2", "Scarlett Johansson", R.drawable.oppenheimer, "Actress"),
            CelebModel("3", "Dwayne Johnson", R.drawable.wolf, "Actor")
        )

        val genreList = listOf(
            GenreModel(28, "Action"),
            GenreModel(12, "Adventure"),
            GenreModel(16, "Animation"),
            GenreModel(35, "Comedy"),
            GenreModel(80, "Crime"),
            GenreModel(18, "Drama"),
            GenreModel(27, "Horror"),
            GenreModel(10749, "Romance"),
            GenreModel(53, "Thriller"),
            GenreModel(37, "Western")
        )

        setContent {
            MovieAppTheme {
                val sharedViewModel: SharedProfileViewModel = viewModel()

                CelebrityGenreScreen(
                    celebrities = mockCelebs,
                    genres = genreList,
                    sharedViewModel = sharedViewModel,
                    onDoneClick = {
                       startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

// ---------------------- CELEBRITY & GENRE SCREEN ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelebrityGenreScreen(
    sharedViewModel: SharedProfileViewModel,
    celebrities: List<CelebModel>,
    genres: List<GenreModel>,
    onDoneClick: () -> Unit
) {
    val favoriteCelebs by sharedViewModel.favoriteCelebs.collectAsState()
    val favoriteGenres by sharedViewModel.favoriteGenres.collectAsState()

    val isButtonEnabled = favoriteCelebs.isNotEmpty() && favoriteGenres.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Favourites", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Celebrities",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(celebrities) { celeb ->
                CelebrityItem(
                    celeb = celeb,
                    isSelected = favoriteCelebs.contains(celeb),
                    onToggleSelected = { sharedViewModel.addOrRemoveCeleb(celeb) }
                )
                Divider(color = Color.DarkGray, thickness = 1.dp)
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Genres",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(genres) { genre ->
                GenreItem(
                    genre = genre,
                    isSelected = favoriteGenres.contains(genre),
                    onToggleSelected = { sharedViewModel.addOrRemoveGenre(genre) }
                )
                Divider(color = Color.DarkGray, thickness = 1.dp)
            }

            item {
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = onDoneClick,
                    enabled = isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonEnabled) Color.Red else Color.Red.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CelebrityItem(
    celeb: CelebModel,
    isSelected: Boolean,
    onToggleSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = celeb.imageRes),
            contentDescription = celeb.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = celeb.name,
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = celeb.role,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        IconButton(onClick = onToggleSelected) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "",
                tint = if (isSelected) Color.Red else Color.White
            )
        }
    }
}

@Composable
fun GenreItem(
    genre: GenreModel,
    isSelected: Boolean,
    onToggleSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = genre.name,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onToggleSelected) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                tint = if (isSelected) Color.Red else Color.White,
                contentDescription = ""
            )
        }
    }
}
