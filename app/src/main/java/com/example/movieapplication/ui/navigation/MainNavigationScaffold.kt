package com.example.movieapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.ui.home.HomeViewModel

@Composable
fun MainNavigationScaffold(
    viewModel: HomeViewModel,
    showBottomBarState: MutableState<Boolean>
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // الشاشات اللي **عاوزين نخفي فيها البوتوم بار**
    val noBottomBarScreens = listOf(
        "splash",
        "login",
        "signup"
    )

    // لو الشاشة الحالية من الشاشات دي → أخفي البار
    showBottomBarState.value = currentRoute !in noBottomBarScreens

    Scaffold(
        bottomBar = {
            if (showBottomBarState.value) {
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
            modifier = Modifier.padding()
        )
    }
}
