package com.example.movieapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.movieapplication.ui.watchlist.CelebModel
import com.example.movieapplication.ui.watchlist.GenreModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedProfileViewModel : ViewModel() {

    private val _favoriteCelebs = MutableStateFlow<List<CelebModel>>(emptyList())
    val favoriteCelebs: StateFlow<List<CelebModel>> = _favoriteCelebs.asStateFlow()

    private val _favoriteGenres = MutableStateFlow<List<GenreModel>>(emptyList())
    val favoriteGenres: StateFlow<List<GenreModel>> = _favoriteGenres.asStateFlow()

    fun addOrRemoveCeleb(celeb: CelebModel) {
        _favoriteCelebs.value = if (_favoriteCelebs.value.contains(celeb))
            _favoriteCelebs.value - celeb
        else
            _favoriteCelebs.value + celeb
    }

    fun addOrRemoveGenre(genre: GenreModel) {
        _favoriteGenres.value = if (_favoriteGenres.value.contains(genre))
            _favoriteGenres.value - genre
        else
            _favoriteGenres.value + genre
    }
}
