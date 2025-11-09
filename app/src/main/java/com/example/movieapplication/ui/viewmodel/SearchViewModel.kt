package com.example.movieapplication.ui.viewmodel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.model.Celebrity
import com.example.movieapp.data.MovieRepository
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

    private val repository = MovieRepository()

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
}
