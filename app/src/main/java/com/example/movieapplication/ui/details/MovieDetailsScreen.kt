package com.example.movieapp.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.model.Movie
import kotlinx.coroutines.launch

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // 🔙 زر الرجوع
        TextButton(onClick = onBackClick) {
            Text("← Back", color = Color.White, fontSize = 16.sp)
        }

        // 🎞️ صورة الفيلم
        Image(
            painter = rememberAsyncImagePainter(
                model = posterPath?.let {
                    if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500/${it.trimStart('/')}"
                } ?: "https://via.placeholder.com/500x750?text=No+Image"
            ),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

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
