package com.example.movieapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccountSettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _settingsState = MutableStateFlow(AccountSettingsState())
    val settingsState: StateFlow<AccountSettingsState> = _settingsState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _settingsState.value = _settingsState.value.copy(
                username = currentUser.displayName ?: "User",
                email = currentUser.email ?: "",
                originalUsername = currentUser.displayName ?: "User"
            )

            loadUserDataFromFirestore(currentUser.uid)
        }
    }

    private fun loadUserDataFromFirestore(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(userId).get().await()
                if (userDoc.exists()) {
                    val userData = userDoc.data
                    val username = userData?.get("username") as? String
                    if (!username.isNullOrEmpty()) {
                        _settingsState.value = _settingsState.value.copy(
                            username = username,
                            originalUsername = username
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUsername(newUsername: String) {
        _settingsState.value = _settingsState.value.copy(
            username = newUsername,
            isUsernameChanged = newUsername != _settingsState.value.originalUsername
        )
    }

    fun saveUsername() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val newUsername = _settingsState.value.username

                db.collection("users").document(userId)
                    .update("username", newUsername)
                    .await()

                _settingsState.value = _settingsState.value.copy(
                    isUsernameChanged = true,
                    originalUsername = newUsername
                )

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCurrentPassword(password: String) {
        _settingsState.value = _settingsState.value.copy(currentPassword = password)
    }

    fun updateNewPassword(password: String) {
        _settingsState.value = _settingsState.value.copy(newPassword = password)
    }

    fun updateConfirmPassword(password: String) {
        _settingsState.value = _settingsState.value.copy(confirmPassword = password)
    }

    fun savePassword() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val state = _settingsState.value
                val user = auth.currentUser ?: throw Exception("User not authenticated")
                val userEmail = user.email ?: throw Exception("User email not found")

                if (state.newPassword != state.confirmPassword) {
                    _settingsState.value = state.copy(
                        passwordError = "New passwords don't match",
                        isPasswordChanged = false
                    )
                    return@launch
                }

                if (state.newPassword.length < 6) {
                    _settingsState.value = state.copy(
                        passwordError = "Password must be at least 6 characters",
                        isPasswordChanged = false
                    )
                    return@launch
                }

                // Re-authenticate user before changing password
                val credential = EmailAuthProvider.getCredential(userEmail, state.currentPassword)
                user.reauthenticate(credential).await()

                // Change password
                user.updatePassword(state.newPassword).await()

                _settingsState.value = state.copy(
                    passwordError = null,
                    isPasswordChanged = true,
                    currentPassword = "",
                    newPassword = "",
                    confirmPassword = ""
                )

            } catch (e: Exception) {
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        _settingsState.value = _settingsState.value.copy(
                            passwordError = "Current password is incorrect",
                            isPasswordChanged = false
                        )
                    }
                    else -> {
                        _settingsState.value = _settingsState.value.copy(
                            passwordError = "Failed to change password: ${e.message}",
                            isPasswordChanged = false
                        )
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}

data class AccountSettingsState(
    val username: String = "",
    val email: String = "",
    val originalUsername: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val isUsernameChanged: Boolean = false,
    val isPasswordChanged: Boolean = false
)