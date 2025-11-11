package com.example.movieapp.ui.home
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.model.Celebrity
import com.example.movieapp.model.Movie
import kotlinx.coroutines.launch
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit = {},
    onCelebrityClick: (Celebrity) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {}
) {
    val trendingMovies by viewModel.trendingMovies.collectAsState()
    val trendingCelebrities by viewModel.trendingCelebrities.collectAsState()
    val watchlistMovies by viewModel.watchlist.collectAsState(initial = emptyList())
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                )
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(bottom = 12.dp)
        ) {
            //  العنوان الرئيسي
            Text(
                text = "Discover Movies",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // 🎬 اللافتة الدعائية
            if (trendingMovies.isNotEmpty()) {
                val topMovie = trendingMovies.first()
                val backdropUrl = topMovie.backdropPath?.let {
                    if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
                    else "https://image.tmdb.org/t/p/w500/$it"
                } ?: "https://via.placeholder.com/500x300?text=No+Image"
                val isInWatchlist = watchlistMovies.any { it.id == topMovie.id }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onMovieClick(topMovie) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(backdropUrl),
                        contentDescription = topMovie.title ?: "Movie banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    // 🎞️ زر التشغيل
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Trailer",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(70.dp)
                            .background(Color.White.copy(alpha = 0.25f), shape = CircleShape)
                            .clip(CircleShape)
                    )
                    // ➕ / ❌ زر قائمة المشاهدة
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Toggle Watchlist",
                        tint = if (isInWatchlist) Color.Red else Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .size(28.dp)
                            .clickable {
                                if (isInWatchlist) {
                                    viewModel.removeFromWatchlist(topMovie)
                                } else {
                                    viewModel.addToWatchlist(topMovie)
                                }
                            }
                    )
                    // 🧾 معلومات الفيلم
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                topMovie.posterPath?.let {
                                    if (it.startsWith("/")) "https://image.tmdb.org/t/p/w200$it"
                                    else "https://image.tmdb.org/t/p/w200/$it"
                                } ?: "https://via.placeholder.com/100x150?text=No+Image"
                            ),
                            contentDescription = topMovie.title ?: "Movie Poster",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = topMovie.title ?: "Untitled",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "⭐ ${topMovie.voteAverage ?: 0.0}",
                                color = Color.Yellow,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 🔍 البحث
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search for Movies , Celebrities..") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { onSearchClick() },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    cursorColor = Color.White
                ),
                enabled = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            // 🍿 الأفلام الشائعة
            SectionTitle("Trending Movies Today")
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(trendingMovies) { movie ->
                    val isInWatchlist = watchlistMovies.any { it.id == movie.id }
                    MovieItem(
                        movie = movie,
                        onClick = onMovieClick,
                        onToggleWatchlist = {
                            if (isInWatchlist) {
                                viewModel.removeFromWatchlist(movie)
                            } else {
                                viewModel.addToWatchlist(movie)
                            }
                        },
                        isInWatchlist = isInWatchlist
                    )
                }
            }
            // 🌟 المشاهير الشائعين
            SectionTitle("Trending Celebrities")
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(trendingCelebrities) { celeb ->
                    CelebrityItem(celeb = celeb, onClick = onCelebrityClick)
                }
            }
            // 🎞️ قائمة المشاهدة
            SectionTitle("My Watchlist")
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(watchlistMovies.take(3)) { movieEntity ->
                    WatchlistItem(
                        movieEntity = movieEntity,
                        onMovieClick = onMovieClick,
                        onRemoveClick = { movie ->
                            viewModel.removeFromWatchlist(movie)
                        }
                    )
                }
            }
            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("View All", color = Color.Red, fontSize = 14.sp)
            }
        }
    }
}
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
@Composable
fun MovieItem(
    movie: Movie,
    onClick: (Movie) -> Unit,
    onToggleWatchlist: (Movie) -> Unit,
    isInWatchlist: Boolean
) {
    val posterUrl = movie.posterPath?.let {
        if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
        else "https://image.tmdb.org/t/p/w500/$it"
    } ?: "https://via.placeholder.com/300x450?text=No+Image"
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
    ) {
        Column(
            modifier = Modifier.clickable { onClick(movie) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(posterUrl),
                contentDescription = movie.title ?: "Movie Poster",
                modifier = Modifier
                    .height(190.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.title ?: "Untitled",
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                text = "⭐ ${movie.voteAverage ?: 0.0}",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = if (isInWatchlist) Icons.Default.Close else Icons.Default.Add,
            contentDescription = "Toggle Watchlist",
            tint = if (isInWatchlist) Color.Red else Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(22.dp)
                .clickable { onToggleWatchlist(movie) }
        )
    }
}

@Composable
fun CelebrityItem(celeb: Celebrity, onClick: (Celebrity) -> Unit) {
    val profileUrl = celeb.profilePath?.let {
        if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
        else "https://image.tmdb.org/t/p/w500/$it"
    } ?: "https://via.placeholder.com/100x100?text=No+Image"
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!celeb.name.isNullOrBlank() && celeb.id != 0) {
                    onClick(celeb)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = celeb.name ?: "Celebrity",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = celeb.name ?: "Unknown",
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text(
            text = celeb.role ?: "N/A",
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}


@Composable
fun WatchlistItem(
    movieEntity: MovieEntity,
    onMovieClick: (Movie) -> Unit,
    onRemoveClick: (Movie) -> Unit
) {
    val posterUrl = movieEntity.posterPath?.let {
        if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
        else "https://image.tmdb.org/t/p/w500/$it"
    } ?: "https://via.placeholder.com/300x450?text=No+Image"

    val movieFromEntity = Movie(
        id = movieEntity.id,
        title = movieEntity.title ?: "Untitled",
        posterPath = movieEntity.posterPath,
        backdropPath = null,
        voteAverage = movieEntity.voteAverage ?: 0.0,
        overview = movieEntity.overview,
        releaseDate = null
    )
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(posterUrl),
            contentDescription = movieEntity.title ?: "Watchlist Movie",
            modifier = Modifier
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onMovieClick(movieFromEntity) },
            contentScale = ContentScale.Crop
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove from Watchlist",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(22.dp)
                .clickable { onRemoveClick(movieFromEntity) }
        )
    }
}
