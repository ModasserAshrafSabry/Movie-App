package com.example.movieapp.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.R
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.model.CastMember
import com.example.movieapp.model.Movie
import com.example.movieapplication.model.CrewMember
import com.example.movieapplication.model.MovieDetails
import kotlinx.coroutines.launch

@Composable
fun MovieDetailsScreen(
    movie: Any,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }

    val title = when (movie) {
        is Movie -> movie.title ?: "Unknown Title"
        is MovieEntity -> movie.title
        else -> "Unknown Title"
    }
    val posterPathProp = when (movie) {
        is Movie -> movie.posterPath
        is MovieEntity -> movie.posterPath
        else -> null
    }
    val voteAverageProp = when (movie) {
        is Movie -> movie.voteAverage
        is MovieEntity -> movie.voteAverage
        else -> null
    }
    val overviewProp = when (movie) {
        is Movie -> movie.overview
        is MovieEntity -> movie.overview
        else -> null
    }
    val id = when (movie) {
        is Movie -> movie.id
        is MovieEntity -> movie.id
        else -> 0
    }

    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) }
    var castList by remember { mutableStateOf<List<CastMember>>(emptyList()) }
    var crewList by remember { mutableStateOf<List<CrewMember>>(emptyList()) }
    var addedToPlaylist by remember { mutableStateOf(movie is MovieEntity) }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        try {
            val repo = MovieRepository()
            movieDetails = repo.getMovieDetails(id)
            val credits = repo.getMovieCredits(id)
            castList = credits.cast
            crewList = credits.crew
        } catch (_: Exception) {}

        try {
            addedToPlaylist = db.watchlistDao().isMovieInWatchlist(id)
        } catch (_: Exception) {}
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        color = Color(0xFF080808)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            DetailsBackdropSection(
                details = movieDetails,
                posterPathFallback = posterPathProp,
                onPlayTrailer = { trailerUrl ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                    context.startActivity(intent)
                },
                onBackClick = onBackClick
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp, start = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.star_icon),
                    contentDescription = "Rating Icon",
                    modifier = Modifier.size(18.dp).padding(end = 6.dp)
                )
                val displayRating = (movieDetails?.voteAverage ?: voteAverageProp)
                displayRating?.let { r ->
                    Text(
                        text = String.format("%.1f/10", r),
                        color = Color(0xFFFFD54F),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = overviewProp ?: movieDetails?.overview ?: "No overview available.",
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (castList.isNotEmpty() || crewList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1c1c1c))
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        ) {
                            Text("|", color = Color(0xFFd8fd33), fontSize = 27.sp)
                            Text(
                                text = "Cast & Crew",
                                fontSize = 25.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        if (castList.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                items(castList.take(10)) { castMember ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(100.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = castMember.profile_path?.let {
                                                    "https://image.tmdb.org/t/p/w200$it"
                                                } ?: "https://via.placeholder.com/100x150?text=No+Image"
                                            ),
                                            contentDescription = castMember.name,
                                            modifier = Modifier
                                                .size(width = 100.dp, height = 150.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
                                            Text(
                                                text = castMember.name ?: "Unknown",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = castMember.character ?: "",
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val directors = crewList.filter { it.job.equals("Director", true) }
                        val writers = crewList.filter { jobIsWriter(it.job) }
                        if (directors.isNotEmpty() || writers.isNotEmpty()) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                if (directors.isNotEmpty()) {
                                    Text("Director", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = directors.joinToString(", ") { it.name ?: "" },
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                if (writers.isNotEmpty()) {
                                    Text("Writers", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = writers.joinToString(", ") { it.name ?: "" },
                                        color = Color.LightGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        if (!addedToPlaylist) {
                            try {
                                db.watchlistDao().addMovie(
                                    MovieEntity(
                                        id = id,
                                        title = title,
                                        posterPath = posterPathProp,
                                        voteAverage = voteAverageProp,
                                        overview = overviewProp
                                    )
                                )
                                addedToPlaylist = true
                                showSnackbar = true
                            } catch (_: Exception) {}
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCFF00)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (addedToPlaylist) "✔ In Playlist" else "+ Add to playlist",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    containerColor = Color.DarkGray
                ) {
                    Text("Added to playlist", color = Color.White)
                }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1800)
                    showSnackbar = false
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DetailsBackdropSection(
    details: MovieDetails?,
    posterPathFallback: String?,
    onPlayTrailer: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val backdropUrl = details?.backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
        ?: "https://via.placeholder.com/780x350?text=No+Backdrop"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(backdropUrl),
            contentDescription = "Backdrop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
        )

        // TITLE + RELEASE DATE + RUNTIME
        details?.let { movie ->
            val year = movie.releaseDate?.take(4) ?: "----"
            val runtimeText = movie.runtime?.let { formatRuntime(it) } ?: ""
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = movie.title ?: "Unknown Title",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$year • $runtimeText",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // BACK BUTTON
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.35f), shape = CircleShape)
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(26.dp))
        }

        // MORE BUTTON


        IconButton(
            onClick = { /* handle click */ },
            modifier = Modifier
                .align(Alignment.TopEnd) // <-- aligns to the top-right
                .padding(16.dp)
                .size(44.dp)
                .background(Color.Black.copy(alpha = 0.35f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }


        // PLAY TRAILER BUTTON
        IconButton(
            onClick = {
                val query = Uri.encode("${details?.title} trailer")
                val searchUrl = "https://www.youtube.com/results?search_query=$query"
                onPlayTrailer(searchUrl)
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp)
                .background(Color.Black.copy(alpha = 0.35f), shape = CircleShape)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play Trailer", tint = Color(0xFFFFD54F), modifier = Modifier.size(44.dp))
        }
    }

    val posterUrl = details?.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
        ?: posterPathFallback?.let { "https://image.tmdb.org/t/p/w500$it" }
        ?: "https://via.placeholder.com/150x225?text=No+Poster"

    Image(
        painter = rememberAsyncImagePainter(posterUrl),
        contentDescription = details?.title ?: "Poster",
        modifier = Modifier
            .offset(x = 16.dp, y = (-80).dp)
            .size(width = 140.dp, height = 210.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop
    )
}

private fun jobIsWriter(job: String?): Boolean {
    if (job == null) return false
    val j = job.lowercase()
    return j.contains("writer") || j.contains("screenplay") || j.contains("story")
}

private fun formatRuntime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
