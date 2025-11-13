package com.example.movieapp.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.movieapp.ui.profile.ProfileScreen
import com.example.movieapp.ui.search.SearchScreen
import com.example.movieapp.ui.settings.AccountSettingsScreen
import com.example.movieapp.ui.watchlist.WatchlistScreen
import com.example.movieapplication.ui.details.CelebrityListScreen
import com.example.movieapplication.ui.details.Genre
import com.example.movieapplication.ui.details.GenresScreen
import com.example.movieapplication.ui.details.MovieGridScreen
import com.example.movieapplication.ui.viewmodel.SearchViewModel
import com.google.gson.Gson
import com.example.movieapplication.ui.details.SeeAllScreen

val genreList = listOf(
    Genre(28, "Action"),
    Genre(12, "Adventure"),
    Genre(16, "Animation"),
    Genre(35, "Comedy"),
    Genre(80, "Crime"),
    Genre(99, "Documentary"),
    Genre(18, "Drama"),
    Genre(10751, "Family"),
    Genre(14, "Fantasy"),
    Genre(36, "History"),
    Genre(27, "Horror"),
    Genre(10402, "Music"),
    Genre(9648, "Mystery"),
    Genre(10749, "Romance"),
    Genre(878, "Science Fiction"),
    Genre(53, "Thriller"),
    Genre(10770, "TV Movie"),
    Genre(10752, "War"),
    Genre(37, "Western")
)

@Composable
fun AppNavigation(viewModel: HomeViewModel) {
    val navController = rememberNavController()
    val gson = Gson()
    val trendingMovies = viewModel.trendingMovies.collectAsState().value
    val trendingCelebrities by viewModel.trendingCelebrities.collectAsState()

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
<<<<<<< Updated upstream
                onViewAllClick = { navController.navigate("watchlist")},
                onSeeAllClicked = {val json = gson.toJson(trendingMovies)
                    val encoded = Uri.encode(json)
                    navController.navigate("SeeAllScreen?movieList=$encoded")
                },
                onCelebSeeAllClick = {val json = gson.toJson(trendingCelebrities)
                    val encoded = Uri.encode(json)
                    navController.navigate("allCelebrities?celebrityList=$encoded")
            })
        }

        composable(
            route = "SeeAllScreen?movieList={movieList}",
            arguments = listOf(navArgument("movieList") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val encodedList = backStackEntry.arguments?.getString("movieList") ?: "[]"
            val json = Uri.decode(encodedList)
            val movies: List<Movie> = runCatching {
                gson.fromJson(json, Array<Movie>::class.java)?.toList() ?: emptyList()
            }.getOrDefault(emptyList())

            SeeAllScreen(
                movies = movies,
                onMovieClick = { movie ->
                    val json = gson.toJson(movie)
                    val encoded = Uri.encode(json)
                    navController.navigate("details/$encoded")
                },
                onBackClick = { navController.popBackStack() }
=======
                onViewAllClick = { navController.navigate("watchlist") },
                onProfileClick = { navController.navigate("profile") }  // Add this line
>>>>>>> Stashed changes
            )
        }

        composable(
            route = "allCelebrities?celebrityList={celebrityList}",
            arguments = listOf(navArgument("celebrityList") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val encodedList = backStackEntry.arguments?.getString("celebrityList") ?: "[]"
            val json = Uri.decode(encodedList)
            val celebrities: List<Celebrity> = runCatching {
                gson.fromJson(json, Array<Celebrity>::class.java)?.toList() ?: emptyList()
            }.getOrDefault(emptyList())

            CelebrityListScreen(
                celebrities = celebrities,
                onCelebrityClick = { celebrity ->
                    val json = gson.toJson(celebrity)
                    val encoded = Uri.encode(json)
                    navController.navigate("celebrityDetails/$encoded")
                },
                onBackClick = { navController.popBackStack() }
            )
        }



        // 🎬 تفاصيل الفيلم
        composable(
            "details/{movieJson}",
            arguments = listOf(navArgument("movieJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieJsonEncoded =
                backStackEntry.arguments?.getString("movieJson") ?: return@composable
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
                },
                onCelebrityClick = { celebrity ->
                    val json = gson.toJson(celebrity)
                    val encoded = Uri.encode(json)
                    navController.navigate("celebrityDetails/$encoded")
                },

                onSeeAllClick = { type ->
                    if (type.startsWith("genre_")) {
                        val genreId = type.removePrefix("genre_").toIntOrNull() ?: 0
                        navController.navigate("moviesByGenre/$genreId")
                    } else {
                        navController.navigate("SeeAllScreen?contentType=$type")
                    }
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
                },
                onRemoveClick = { movieEntity ->
                    val movie = com.example.movieapp.model.Movie(
                        id = movieEntity.id,
                        title = movieEntity.title,
                        posterPath = movieEntity.posterPath,
                        backdropPath = null,
                        voteAverage = movieEntity.voteAverage,
                        overview = movieEntity.overview,
                        releaseDate = null
                    )
                    viewModel.removeFromWatchlist(movie)
                }
            )
        }

        composable(
            route = "SeeAllScreen?contentType={contentType}",
            arguments = listOf(navArgument("contentType") {
                defaultValue = "movies"
                nullable = true
            })
        ) { backStackEntry ->
            val contentType = backStackEntry.arguments?.getString("contentType") ?: "movies"
            val searchViewModel: SearchViewModel = viewModel()

            when (contentType) {
                "movies" -> {
                    MovieGridScreen(
                        genreId = 0,
                        searchViewModel = searchViewModel,
                        onMovieClick = { movie ->
                            val json = gson.toJson(movie)
                            val encoded = Uri.encode(json)
                            navController.navigate("details/$encoded")
                        }
                    )
                }

                "genres" -> {
                    GenresScreen(
                        genres = genreList,
                        onGenreSelected = { genre ->
                            navController.navigate("moviesByGenre/${genre.id}")
                        }
                    )
                }
            }
        }
        composable(
            route = "moviesByGenre/{genreId}",
            arguments = listOf(navArgument("genreId") { type = NavType.IntType })
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
            val searchViewModel: SearchViewModel = viewModel()

            MovieGridScreen(
                genreId = genreId,
                searchViewModel = searchViewModel,
                onMovieClick = { movie ->
                    val json = gson.toJson(movie)
                    val encoded = Uri.encode(json)
                    navController.navigate("details/$encoded")
                }
            )
        }

        composable(
            "celebrityDetails/{celebrityJson}",
            arguments = listOf(navArgument("celebrityJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val celebJsonEncoded =
                backStackEntry.arguments?.getString("celebrityJson") ?: return@composable
            val celebJson = Uri.decode(celebJsonEncoded)
            val celebrity =
                runCatching { gson.fromJson(celebJson, Celebrity::class.java) }.getOrNull()
                    ?: return@composable

            CelebrityDetailsScreen(
                celebrity = celebrity,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 👤 Profile Screen
        composable("profile") {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("account_settings") },
                onNavigateToCelebrity = { celebrityId ->
                    // Handle celebrity navigation
                },
                onNavigateToGenre = { genre ->
                    // Handle genre navigation
                }
            )
        }

        // ⚙️ Account Settings Screen
        composable("account_settings") {
            AccountSettingsScreen(
                onLogout = {
                    // Navigate to login/splash
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
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
                voteAverage = entity.voteAverage,
                backdropPath = null,
                releaseDate = null
            )
        } catch (e: Exception) {
            null
        }
    }
}