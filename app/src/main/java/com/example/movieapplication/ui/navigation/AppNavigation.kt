package com.example.movieapp.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieapp.model.Movie
import com.example.movieapp.model.Celebrity
import com.example.movieapp.data.local.MovieEntity
import com.example.movieapp.ui.details.MovieDetailsScreen
import com.example.movieapp.ui.celebrity.CelebrityDetailsScreen
import com.example.movieapp.ui.home.HomeScreen
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.search.SearchScreen
import com.example.movieapp.ui.watchlist.WatchlistScreen
import com.google.gson.Gson

@Composable
fun AppNavigation(viewModel: HomeViewModel) {
    val navController = rememberNavController()
    val gson = Gson()

    val trendingMovies = viewModel.trendingMovies.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier
    ) {

        // 🏠 الشاشة الرئيسية
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onMovieClick = { movie ->
                    val json = gson.toJson(movie)
                    val encoded = Uri.encode(json)
                    navController.navigate("details/$encoded")
                },
                onCelebrityClick = { celebrity ->
                    val json = gson.toJson(celebrity)
                    val encoded = Uri.encode(json)
                    navController.navigate("celebrityDetails/$encoded")
                },
                onSearchClick = { navController.navigate("search") },
                onViewAllClick = { navController.navigate("watchlist") }
            )
        }

        // 🎬 تفاصيل الفيلم
        composable(
            "details/{movieJson}",
            arguments = listOf(navArgument("movieJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieJsonEncoded = backStackEntry.arguments?.getString("movieJson") ?: return@composable
            val movieJson = Uri.decode(movieJsonEncoded)
            val movie = decodeMovieJson(movieJson, gson) ?: return@composable

            MovieDetailsScreen(
                movie = movie,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 🔍 شاشة البحث
        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onMovieClick = { movie ->
                    val json = gson.toJson(movie)
                    val encoded = Uri.encode(json)
                    navController.navigate("details/$encoded")
                }
            )
        }

        // 🎞️ قائمة المشاهدة
        composable("watchlist") {
            val watchlist = viewModel.watchlist.collectAsState(initial = emptyList()).value
            WatchlistScreen(
                watchlist = watchlist,
                onBackClick = { navController.popBackStack() },
                onMovieClick = { movieEntity ->
                    val json = gson.toJson(movieEntity)
                    val encoded = Uri.encode(json)
                    navController.navigate("details/$encoded")
                }
            )
        }

        // 🌟 تفاصيل المشاهير
        composable(
            "celebrityDetails/{celebrityJson}",
            arguments = listOf(navArgument("celebrityJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val celebJsonEncoded = backStackEntry.arguments?.getString("celebrityJson") ?: return@composable
            val celebJson = Uri.decode(celebJsonEncoded)
            val celebrity = runCatching { gson.fromJson(celebJson, Celebrity::class.java) }.getOrNull() ?: return@composable

            CelebrityDetailsScreen(
                celebrity = celebrity,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// 🧠 دالة بتحول JSON إلى Movie أو MovieEntity
private fun decodeMovieJson(json: String, gson: Gson): Movie? {
    return try {
        gson.fromJson(json, Movie::class.java)
    } catch (e: Exception) {
        try {
            val entity = gson.fromJson(json, MovieEntity::class.java)
            Movie(
                id = entity.id,
                title = entity.title,
                overview = entity.overview,
                posterPath = entity.posterPath,
                backdropPath = entity.posterPath ?: "",
                voteAverage = entity.voteAverage,
                releaseDate = null
            )
        } catch (e: Exception) {
            null
        }
    }
}
