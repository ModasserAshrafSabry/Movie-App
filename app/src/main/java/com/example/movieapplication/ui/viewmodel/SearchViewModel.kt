package com.example.movieapplication.ui.viewmodel

import android.media.ToneGenerator
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.model.Celebrity
import com.example.movieapp.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    var query = mutableStateOf("")
    var movieSearchResults = mutableStateOf<List<Movie>>(emptyList())
    var celebSearchResults = mutableStateOf<List<Celebrity>>(emptyList())
    var suggestions = mutableStateOf<List<String>>(emptyList())
    var showSuggestions = mutableStateOf(false)
    var isLoading = mutableStateOf(false)
    var errorMsg = mutableStateOf<String?>(null)
    var userClickedSuggestion = mutableStateOf(false)

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies.asStateFlow()

    private val _MoviesByGenre = MutableStateFlow<List<Movie>>(emptyList())
    val MoviesByGenre: StateFlow<List<Movie>> = _MoviesByGenre.asStateFlow()

    private val repository = MovieRepository()

    init {
        fetchPopularMovies()
    }


    fun onQueryChanged(newQuery: String) {
        query.value = newQuery
        if (userClickedSuggestion.value) {
            userClickedSuggestion.value = false
            return
        }
        if (newQuery.length < 1) {
            suggestions.value = emptyList()
            showSuggestions.value = false
            return
        }
        viewModelScope.launch {
            try {
                val movieResponse = repository.searchMovies(newQuery.trim())
                val celebResponse = repository.searchCelebrities(newQuery.trim())
                val movieSuggestions = movieResponse.results.take(3).mapNotNull { it.title }
                val celebSuggestions = celebResponse.results.take(2).mapNotNull { it.name }
                val allSuggestions = (movieSuggestions + celebSuggestions).distinct()
                suggestions.value = allSuggestions
                showSuggestions.value = allSuggestions.isNotEmpty()
            } catch (e: Exception) {
                suggestions.value = emptyList()
                showSuggestions.value = false
            }
        }
    }

    fun performSearch(searchQuery: String = query.value) {
        if (searchQuery.isBlank()) return
        showSuggestions.value = false
        viewModelScope.launch {
            isLoading.value = true
            errorMsg.value = null
            try {
                val movResponse = repository.searchMovies(searchQuery.trim())
                val celebResponse = repository.searchCelebrities(searchQuery.trim())
                movieSearchResults.value = movResponse.results
                celebSearchResults.value = celebResponse.results
            } catch (e: Exception) {
                errorMsg.value = "Something went wrong. Please try again."
                movieSearchResults.value = emptyList()
                celebSearchResults.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun onSuggestionClicked(suggestion: String) {
        query.value = suggestion
        userClickedSuggestion.value = true
        performSearch(suggestion)
        showSuggestions.value = false
    }

    fun clearAll() {
        query.value = ""
        movieSearchResults.value = emptyList()
        celebSearchResults.value = emptyList()
        suggestions.value = emptyList()
        showSuggestions.value = false
        errorMsg.value = null
    }

    fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                _popularMovies.value = repository.getPopularMovies()?.results ?: emptyList()
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching Popular data: ${e.message}")
            }
        }
    }

    fun OnGenreSelected(genreId: Int) {
        viewModelScope.launch {
            try {
                _MoviesByGenre.value = repository.getMovieByGenre(genreId)?.results ?: emptyList()
                Log.d("SearchViewModel", "Fetched ${_MoviesByGenre.value.size} movies for genreId $genreId")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching movies by genre: ${e.message}")
            }
        }
    }

}
