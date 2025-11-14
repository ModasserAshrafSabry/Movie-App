package com.example.movieapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.ui.home.HomeViewModel

@Composable
fun MainNavigationScaffold(
    viewModel: HomeViewModel
) {
    val navController = rememberNavController()

    // نعرف الشاشة الحالية
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // ⛔ الشاشات اللي فعلاً جوه الـ AppNavigation وعايزين نخبي فيها البوتوم بار
    val bottomBarRoutes = listOf(
        "home",
        "search",
        "watchlist",
        "profile"
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(
            viewModel = viewModel,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
