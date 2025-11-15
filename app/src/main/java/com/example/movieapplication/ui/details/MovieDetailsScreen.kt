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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.model.CastMember
import com.example.movieapp.model.Movie
import com.example.movieapplication.model.CrewMember
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty

@Composable
fun MovieDetailsScreen(
    movie: Any,                // 🔹 بيقبل Movie أو MovieEntity
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }

    // 🔍 استخراج البيانات حسب النوع (Movie / MovieEntity)
    val title = when (movie) {
        is Movie -> movie.title ?: "Unknown Title"
        is MovieEntity -> movie.title
        else -> "Unknown Title"
    }
    val posterPath = when (movie) {
        is Movie -> movie.posterPath
        is MovieEntity -> movie.posterPath
        else -> null
    }
    val voteAverage = when (movie) {
        is Movie -> movie.voteAverage
        is MovieEntity -> movie.voteAverage
        else -> null
    }
    val overview = when (movie) {
        is Movie -> movie.overview
        is MovieEntity -> movie.overview
        else -> null
    }
    val id = when (movie) {
        is Movie -> movie.id
        is MovieEntity -> movie.id
        else -> 0
    }

    var addedToWatchlist by remember { mutableStateOf(movie is MovieEntity) }
    var showMessage by remember { mutableStateOf(false) }

    // ✅ تحقق لو الفيلم بالفعل مضاف في الـ Watchlist
    LaunchedEffect(Unit) {
        val exists = db.watchlistDao().isMovieInWatchlist(id)
        addedToWatchlist = exists
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // 🔙 زر الرجوع
        TextButton(onClick = onBackClick) {
            Text("← Back", color = Color.White, fontSize = 16.sp)
        }

        // 🎬 عرض صورة الخلفية + أيقونة التشغيل
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
        ) {
            // الخلفية (backdrop)
            val backdropUrl = when (movie) {
                is Movie -> movie.backdropPath?.let {
                    "https://image.tmdb.org/t/p/w780/${it.trimStart('/')}"
                }

                else -> null
            } ?: "https://via.placeholder.com/500x300?text=No+Image"

            Image(
                painter = rememberAsyncImagePainter(backdropUrl),
                contentDescription = "Backdrop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // طبقة شفافة فوق الصورة
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            // زر التشغيل (التريلر)
            IconButton(
                onClick = {
                    val trailerUrl = "https://www.youtube.com/results?search_query=${
                        movie.let {
                            if (it is Movie) it.title else "movie trailer"
                        }
                    }"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.25f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Trailer",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

// 🎞️ البوستر (خارج الـ Box علشان ما يتقصش)
        val posterUrl = when (movie) {
            is Movie -> movie.posterPath?.let {
                "https://image.tmdb.org/t/p/w500/${it.trimStart('/')}"
            }

            else -> null
        } ?: "https://via.placeholder.com/150x200?text=No+Image"

        Image(
            painter = rememberAsyncImagePainter(posterUrl),
            contentDescription = "Poster",
            modifier = Modifier
                .offset(x = 16.dp, y = (-70).dp) // يطلع فوق شوية عشان يتداخل مع الخلفية
                .size(width = 120.dp, height = 180.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        )

        // 🎬 عنوان الفيلم
        Text(text = title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // ⭐ التقييم
        voteAverage?.let {
            Text(
                text = "⭐ $it/10",
                color = Color.Yellow,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🧾 وصف الفيلم
        Text(
            text = overview ?: "No overview available.",
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
// بعد Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.height(24.dp))

// 🧑‍🎤 Cast & Crew
        val castList = remember { mutableStateOf<List<CastMember>>(emptyList()) }
        val crewList = remember { mutableStateOf<List<CrewMember>>(emptyList()) }

        LaunchedEffect(id) {
            // لو movie من نوع Movie، هنجلب الـ credits من الريبو أو API
            if (movie is Movie) {
                try {
                    val repo = MovieRepository()  // أو inject لو عندك DI
                    val credits =
                        repo.getMovieCredits(movie.id)  // دالة جديدة في الريبو لازم تعملها
                    castList.value = credits.cast
                    crewList.value = credits.crew
                } catch (e: Exception) {
                    // فشل في جلب الـ credits
                }
            }
        }

// 🎭 Cast
        // 🎭 Cast Section - LazyRow
        if (castList.value.isNotEmpty()) {
            Text(
                text = "Cast",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(castList.value.take(10)) { cast ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(100.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = cast.profile_path?.let {
                                    if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w200/${
                                        it.trimStart(
                                            '/'
                                        )
                                    }"
                                } ?: "https://via.placeholder.com/100x150?text=No+Image"
                            ),
                            contentDescription = cast.name,
                            modifier = Modifier
                                .size(width = 100.dp, height = 150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = cast.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            maxLines = 1
                        )

                        Text(
                            text = cast.character,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

// 🎬 Crew
        if (!crewList.value.isNullOrEmpty()) {
            Text(
                text = "Crew",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                crewList.value.take(5).forEach { crew ->
                    Text(
                        text = "${crew.job ?: "Job"}: ${crew.name}",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    if (!addedToWatchlist) {
                        val entity = MovieEntity(id, title, posterPath, voteAverage, overview)
                        db.watchlistDao().addMovie(entity)
                        addedToWatchlist = true
                        showMessage = true
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (addedToWatchlist) Color.Gray else Color.Red
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                if (addedToWatchlist) "✔ In Watchlist" else "➕ Add to Watchlist",
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(120.dp))


        // ✅ رسالة تأكيد مؤقتة
        if (showMessage) {
            Snackbar(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally),
                containerColor = Color.DarkGray
            ) {
                Text("Added to Watchlist!", color = Color.White, fontSize = 16.sp)
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showMessage = false
            }
        }
    }
}
