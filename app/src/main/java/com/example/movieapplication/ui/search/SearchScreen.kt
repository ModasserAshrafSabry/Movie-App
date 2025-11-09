package com.example.movieapp.ui.search

// ✅ استيراد الأدوات الأساسية من Jetpack Compose
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.model.Movie
import com.example.movieapp.ui.home.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: HomeViewModel, // 💡 علشان نقدر نتعامل مع الـ Watchlist
    onBackClick: () -> Unit = {}, // زر الرجوع
    onMovieClick: (Movie) -> Unit = {} // لما المستخدم يضغط على فيلم
) {
    val repository = remember { MovieRepository() } // ⚙️ إنشاء Repository للبحث
    val coroutineScope = rememberCoroutineScope() // لإطلاق عمليات غير متزامنة (Coroutines)

    // 🧩 حالة واجهة المستخدم
    var query by remember { mutableStateOf("") } // النص المكتوب في مربع البحث
    var searchResults by remember { mutableStateOf<List<Movie>>(emptyList()) } // النتائج
    var isLoading by remember { mutableStateOf(false) } // حالة التحميل
    var errorMsg by remember { mutableStateOf<String?>(null) } // رسالة الخطأ

    // 📡 مراقبة قائمة المشاهدة
    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())

    // 🎨 واجهة المستخدم
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {

        // 🔙 عنوان الشاشة وزر الرجوع
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Search Movies",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔍 مربع البحث
        OutlinedTextField(
            value = query,
            onValueChange = { query = it }, // تحديث النص أثناء الكتابة
            placeholder = { Text("Search for a movie...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // لما المستخدم يضغط Enter في الكيبورد
                    if (query.isBlank()) return@KeyboardActions
                    coroutineScope.launch {
                        isLoading = true
                        errorMsg = null
                        try {
                            val response = repository.searchMovies(query.trim())
                            searchResults = response.results // ✅ حفظ النتائج
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errorMsg = "Something went wrong. Please try again."
                            searchResults = emptyList()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔘 زر البحث
        Button(
            onClick = {
                if (query.isBlank()) return@Button
                coroutineScope.launch {
                    isLoading = true
                    errorMsg = null
                    try {
                        val response = repository.searchMovies(query.trim())
                        searchResults = response.results
                    } catch (e: Exception) {
                        e.printStackTrace()
                        errorMsg = "Something went wrong. Please try again."
                        searchResults = emptyList()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Search", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔄 عرض الحالات المختلفة (تحميل / خطأ / نتائج)
        when {
            isLoading -> {
                // 🌀 حالة التحميل
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            errorMsg != null -> {
                // ⚠️ حالة الخطأ
                Text(
                    text = errorMsg ?: "",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    textAlign = TextAlign.Center
                )
            }

            searchResults.isNotEmpty() -> {
                // ✅ عرض النتائج في قائمة
                LazyColumn {
                    items(searchResults) { movie ->
                        val inWatchlist = watchlist.any { it.id == movie.id } // تحقق هل الفيلم مضاف

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onMovieClick(movie) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 🖼️ صورة الفيلم
                            val posterUrl = movie.posterPath?.let {
                                if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
                                else "https://image.tmdb.org/t/p/w500/$it"
                            } ?: "https://via.placeholder.com/150x225?text=No+Image"

                            Image(
                                painter = rememberAsyncImagePainter(posterUrl),
                                contentDescription = movie.title ?: "Movie poster",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // 🧾 بيانات الفيلم
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = movie.title ?: "Untitled",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "⭐ ${movie.voteAverage ?: 0.0}",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            // ➕ / ❌ زر الإضافة أو الإزالة من الـ Watchlist
                            IconButton(
                                onClick = {
                                    if (inWatchlist) viewModel.removeFromWatchlist(movie)
                                    else viewModel.addToWatchlist(movie)
                                }
                            ) {
                                Icon(
                                    imageVector = if (inWatchlist) Icons.Default.Delete else Icons.Default.Add,
                                    contentDescription = if (inWatchlist) "Remove from Watchlist" else "Add to Watchlist",
                                    tint = if (inWatchlist) Color.Red else Color.White
                                )
                            }
                        }
                        Divider(color = Color.DarkGray)
                    }
                }
            }

            query.isNotEmpty() -> {
                // 😔 لا توجد نتائج
                Text(
                    text = "😔 No results found for \"$query\"",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                )
            }

            else -> {
                // 📭 لم يتم البحث بعد
                Text(
                    text = "Type a movie name and press Search",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
