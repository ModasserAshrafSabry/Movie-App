package com.example.movieapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.ui.home.HomeViewModel
import androidx.compose.ui.unit.dp


@Composable
fun MainNavigationScaffold(
    viewModel: HomeViewModel,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect Snackbar messages from ViewModel
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    // Show Snackbar when message updates
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val bottomBarRoutes = listOf("home", "search", "watchlist", "profile")
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(
                    bottom = if (showBottomBar) 2.dp else 0.dp
                )
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {innerPadding ->
        AppNavigation(
            viewModel = viewModel,
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogout = onLogout
        )
    }
}
