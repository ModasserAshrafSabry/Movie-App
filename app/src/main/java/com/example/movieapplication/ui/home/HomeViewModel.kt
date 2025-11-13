package com.example.movieapp.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.data.local.WatchlistRepository
import com.example.movieapp.model.Movie
import com.example.movieapp.model.Celebrity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val trendingMovies: StateFlow<List<Movie>> = _trendingMovies.asStateFlow()

    private val _trendingCelebrities = MutableStateFlow<List<Celebrity>>(emptyList())
    val trendingCelebrities: StateFlow<List<Celebrity>> = _trendingCelebrities.asStateFlow()

    val watchlist = watchlistRepository.getAllMovies()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    init {
        fetchTrendingData()
    }

    private fun fetchTrendingData() {
        viewModelScope.launch {
            try {
                val moviesResponse = movieRepository.getTrendingMovies()
                val celebsResponse = movieRepository.getTrendingCelebrities()
                _trendingMovies.value = moviesResponse?.results ?: emptyList()
                _trendingCelebrities.value = celebsResponse?.results
                    ?.filter { it.name != null && it.profilePath != null }
                    ?.map {
                        it.copy(
                            name = it.name ?: "Unknown",
                            profilePath = it.profilePath ?: "",
                            role = it.role ?: "N/A"
                        )
                    } ?: emptyList()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching trending data: ${e.message}")
            }
        }
    }

    fun addToWatchlist(movie: Movie) {
        viewModelScope.launch {
            val exists = watchlistRepository.isMovieInWatchlist(movie.id)
            if (exists) {
                _snackbarMessage.value = "⚠️ Movie already in Watchlist"
                return@launch
            }
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title ?: "Untitled",
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                overview = movie.overview
            )
            watchlistRepository.addMovie(entity)
            _snackbarMessage.value = "✅ Added to Watchlist: ${movie.title}"
        }
    }
    // ❌ إزالة فيلم من قائمة المشاهدة
    fun removeFromWatchlist(movie: Movie) {
        viewModelScope.launch {
            val entity = MovieEntity(
                id = movie.id,
                title = movie.title ?: "Untitled",
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                overview = movie.overview
            )
            watchlistRepository.removeMovie(entity)
            _snackbarMessage.value = "❌ Removed from Watchlist: ${movie.title}"
        }
    }
    // 🧹 دالة لمسح رسالة الـ Snackbar بعد عرضها في الواجهة
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
