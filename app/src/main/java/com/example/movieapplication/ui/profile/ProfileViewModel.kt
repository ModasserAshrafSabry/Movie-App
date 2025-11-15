package com.example.movieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProfile()
    }

    fun clearProfileData() {
        // Reset profile state to initial values
        _profileState.value = ProfileState()
        _isLoading.value = false
    }

    // Call this when user logs in with a different account
    fun reloadProfile() {
        clearProfileData()
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = auth.currentUser

                // ✅ ADD THIS CHECK
                if (currentUser == null || !currentUser.isEmailVerified) {
                    _profileState.value = ProfileState(
                        username = "Please login",
                        email = "",
                        favoriteGenres = emptyList(),
                        favoriteCelebrities = emptyList()
                    )
                    return@launch
                }

                if (currentUser != null) {
                    // Get user data from Firestore
                    val userDoc = db.collection("users").document(currentUser.uid).get().await()

                    if (userDoc.exists()) {
                        val userData = userDoc.data
                        _profileState.value = ProfileState(
                            username = userData?.get("username") as? String ?: currentUser.displayName ?: "User",
                            email = currentUser.email ?: "",
                            profileImageUrl = userData?.get("profileImageUrl") as? String ?: "",
                            favoriteGenres = (userData?.get("favoriteGenres") as? List<String>) ?: emptyList(),
                            favoriteCelebrities = (userData?.get("favoriteCelebrities") as? List<Map<String, String>>)?.map {
                                FavoriteCelebrity(
                                    id = it["id"] ?: "",
                                    name = it["name"] ?: "",
                                    role = it["role"] ?: "",
                                    imageUrl = it["imageUrl"] ?: ""
                                )
                            } ?: emptyList()
                        )
                    } else {
                        // Create new user document if it doesn't exist
                        createUserDocument(currentUser.uid)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // If Firebase fails, show empty state
                _profileState.value = ProfileState(
                    username = "Error loading profile",
                    email = "",
                    favoriteGenres = emptyList(),
                    favoriteCelebrities = emptyList()
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun createUserDocument(userId: String) {
        val currentUser = auth.currentUser

        // Use email prefix as temporary username until we get real data
        val tempUsername = currentUser?.email?.substringBefore("@") ?: "User"

        val userData = hashMapOf(
            "username" to tempUsername,
            "email" to (currentUser?.email ?: ""),
            "favoriteGenres" to emptyList<String>(),
            "favoriteCelebrities" to emptyList<Map<String, String>>(),
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(userId).set(userData).await()

        _profileState.value = ProfileState(
            username = tempUsername,
            email = currentUser?.email ?: "",
            profileImageUrl = "",
            favoriteGenres = emptyList(),
            favoriteCelebrities = emptyList()
        )
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                db.collection("users").document(userId)
                    .update("username", newUsername)
                    .await()

                _profileState.value = _profileState.value.copy(username = newUsername)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addFavoriteGenre(genre: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val currentGenres = _profileState.value.favoriteGenres.toMutableList()

                if (!currentGenres.contains(genre)) {
                    currentGenres.add(genre)
                    db.collection("users").document(userId)
                        .update("favoriteGenres", currentGenres)
                        .await()

                    _profileState.value = _profileState.value.copy(favoriteGenres = currentGenres)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class ProfileState(
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val favoriteGenres: List<String> = emptyList(),
    val favoriteCelebrities: List<FavoriteCelebrity> = emptyList()
)

data class FavoriteCelebrity(
    val id: String,
    val name: String,
    val role: String,
    val imageUrl: String = ""
)