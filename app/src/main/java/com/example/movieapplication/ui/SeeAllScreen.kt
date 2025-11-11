package com.example.movieapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.movieapp.model.Movie
import com.example.movieapplication.ui.viewmodel.SearchViewModel


@Composable
fun MovieGridScreen(
    genreId: Int,
    searchViewModel: SearchViewModel = viewModel(),
    onMovieClick: (Movie) -> Unit = {}
) {
    val popularMovies by searchViewModel.popularMovies.collectAsState()
    val moviesByGenre by searchViewModel.MoviesByGenre.collectAsState()

    LaunchedEffect(genreId) {
        if (genreId != 0) {
            searchViewModel.OnGenreSelected(genreId)
        }
    }

    val moviesToDisplay = if (genreId == 0) popularMovies.take(18) else moviesByGenre.take(18)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = if (genreId == 0) "See all" else "Movies by Genre",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(moviesToDisplay.chunked(2)) { rowMovies ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowMovies.forEach { movie ->
                        val posterUrl = movie.posterPath?.let { path ->
                            if (path.startsWith("/")) "https://image.tmdb.org/t/p/w500$path"
                            else "https://image.tmdb.org/t/p/w500/$path"
                        } ?: "https://via.placeholder.com/300x450?text=No+Image"

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(2f / 3f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onMovieClick(movie) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(posterUrl),
                                contentDescription = "Movie poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    if (rowMovies.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
