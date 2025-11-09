package com.example.movieapp.ui.search

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.R
import com.example.movieapp.model.Movie
import com.example.movieapp.model.Celebrity
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapplication.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel(),
    viewModel: HomeViewModel,
    onBackClick: () -> Unit = {},
    onMovieClick: (Movie) -> Unit = {},
    onCelebrityClick: (Celebrity) -> Unit = {}
) {
    val query by searchViewModel.query
    val suggestions by searchViewModel.suggestions
    val showSuggestions by searchViewModel.showSuggestions
    val movieSearchResults by searchViewModel.movieSearchResults
    val celebSearchResults by searchViewModel.celebSearchResults
    val isLoading by searchViewModel.isLoading
    val errorMsg by searchViewModel.errorMsg

    val watchlist by viewModel.watchlist.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Search",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { searchViewModel.onQueryChanged(it) },
                    placeholder = { Text("Search for a movie or Celebrity...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.clearAll() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { searchViewModel.performSearch() }
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
                        unfocusedTextColor = Color.Gray
                    ),
                    singleLine = true
                )
                if (showSuggestions && suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2C2C2E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            suggestions.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchViewModel.onSuggestionClicked(suggestion)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = suggestion,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                                if (suggestion != suggestions.last()) {
                                    Divider(color = Color.DarkGray, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { searchViewModel.performSearch() },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Search", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            errorMsg != null -> {
                Text(
                    text = errorMsg ?: "",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    textAlign = TextAlign.Center
                )
            }
            movieSearchResults.isNotEmpty() || celebSearchResults.isNotEmpty() -> {
                LazyColumn {
                    if (movieSearchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Movies",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(movieSearchResults) { movie ->
                            val inWatchlist = watchlist.any { it.id == movie.id }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { onMovieClick(movie) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val posterUrl = movie.posterPath?.let {
                                    if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
                                    else "https://image.tmdb.org/t/p/w500/$it"
                                }
                                Image(
                                    painter = if (posterUrl != null) {
                                        rememberAsyncImagePainter(posterUrl)
                                    } else {
                                        painterResource(id = R.drawable.no_image)
                                    },
                                    contentDescription = movie.title ?: "Movie poster",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = movie.title ?: "Untitled",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "⭐ ${String.format("%.2f", movie.voteAverage ?: 0.0)}",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (inWatchlist) viewModel.removeFromWatchlist(movie)
                                        else viewModel.addToWatchlist(movie)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (inWatchlist) Icons.Default.Delete else Icons.Default.Add,
                                        contentDescription = if (inWatchlist) "Remove" else "Add",
                                        tint = if (inWatchlist) Color.Red else Color.White
                                    )
                                }
                            }
                            Divider(color = Color.DarkGray)
                        }
                    }
                    if (celebSearchResults.isNotEmpty()) {
                        item {
                            Text(
                                text = "Celebrities",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(celebSearchResults) { celebrity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { onCelebrityClick(celebrity) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val profileUrl = celebrity.profilePath?.let {
                                    if (it.startsWith("/")) "https://image.tmdb.org/t/p/w500$it"
                                    else "https://image.tmdb.org/t/p/w500/$it"
                                }
                                Image(
                                    painter = if (profileUrl != null) {
                                        rememberAsyncImagePainter(profileUrl)
                                    } else {
                                        painterResource(id = R.drawable.no_image)
                                    },
                                    contentDescription = celebrity.name ?: "Celebrity",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = celebrity.name ?: "Unknown",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2
                                    )
                                    Text(
                                        text = celebrity.role ?: "Unknown",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Divider(color = Color.DarkGray)
                        }
                    }
                }
            }
            query.isNotEmpty() -> {
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
                Text(
                    text = "Type a name and press Search",
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
