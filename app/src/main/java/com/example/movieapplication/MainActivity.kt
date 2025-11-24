package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.data.MovieRepository
import com.example.movieapp.data.local.AppDatabase
import com.example.movieapp.data.local.WatchlistRepository
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.home.HomeViewModelFactory
import com.example.movieapp.ui.navigation.MainNavigationScaffold
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapp.viewmodel.WatchlistViewModel
import com.example.movieapp.viewmodel.WatchlistViewModelFactory
import com.example.movieapplication.ui.Login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()

    private val homeViewModel: HomeViewModel by lazy {
        val movieRepository = MovieRepository()
        val database = AppDatabase.getDatabase(application)
        val watchlistRepository = WatchlistRepository(database.watchlistDao())

        ViewModelProvider(
            this,
            HomeViewModelFactory(movieRepository, watchlistRepository)
        )[HomeViewModel::class.java]
    }

    private val watchlistViewModel: WatchlistViewModel by lazy {
        ViewModelProvider(
            this,
            WatchlistViewModelFactory(application)
        )[WatchlistViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser == null || !currentUser.isEmailVerified) {
            navigateToLogin()
            return
        }

        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavigationScaffold(
                        viewModel = homeViewModel,
                        onLogout = {
                            handleLogout()
                        }
                    )
                }
            }
        }
    }

    private fun handleLogout() {
        auth.signOut()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        if (currentUser == null || !currentUser.isEmailVerified) {
            navigateToLogin()
        }
    }
}