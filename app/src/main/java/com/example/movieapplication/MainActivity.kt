package com.example.movieapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance() // Add Firebase Auth

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

        enableEdgeToEdge()

        // ✅ ADD AUTHENTICATION CHECK HERE
        val currentUser = auth.currentUser
        if (currentUser == null || !currentUser.isEmailVerified) {
            // Redirect to login if not authenticated or not verified
            startActivity(Intent(this, com.example.movieapplication.ui.Login.LoginActivity::class.java))
            finish()
            return
        }

        setContent {

            // ⬅️ متغير نقدر نتحكم منه في إظهار وإخفاء الـ BottomBar
            val showBottomBarState = remember { mutableStateOf(true) }

            MovieAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    MainNavigationScaffold(
                        viewModel = homeViewModel,
                        showBottomBarState = showBottomBarState
                    )
                }
            }
        }
    }
}