package com.example.yandexmusic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yandexmusic.core.data.AuthRepository
import com.example.yandexmusic.core.data.MusicRepository
import com.example.yandexmusic.core.data.PlayerService
import com.example.yandexmusic.core.data.TokenStorage
import com.example.yandexmusic.core.network.ApiClient
import com.example.yandexmusic.core.ui.theme.YandexMusicTheme
import com.example.yandexmusic.feature.auth.AuthScreen
import com.example.yandexmusic.feature.auth.AuthViewModel
import com.example.yandexmusic.feature.home.HomeScreen
import com.example.yandexmusic.feature.home.HomeViewModel
import com.example.yandexmusic.feature.library.LibraryScreen
import com.example.yandexmusic.feature.search.SearchScreen
import com.example.yandexmusic.navigation.Screen
import com.example.yandexmusic.navigation.bottomNavItems

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStorage = TokenStorage(this)
        val authRepository = AuthRepository(tokenStorage)

        setContent {
            YandexMusicTheme {
                var isLoggedIn by remember { mutableStateOf(tokenStorage.isLoggedIn) }

                ApiClient.onUnauthorized = {
                    tokenStorage.clear()
                    Handler(Looper.getMainLooper()).post {
                        isLoggedIn = false
                    }
                }

                if (isLoggedIn) {
                    remember {
                        ApiClient.setToken(tokenStorage.musicToken!!)
                        true
                    }
                    MainApp()
                } else {
                    val authViewModel = remember { AuthViewModel(authRepository) }
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = { token ->
                            ApiClient.setToken(token)
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val context = LocalContext.current.applicationContext
        val repository = remember { MusicRepository() }
        val playerService = remember { PlayerService(repository) }
        val homeViewModel = remember { HomeViewModel(repository, playerService, context) }

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel = homeViewModel) }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Library.route) { LibraryScreen() }
        }
    }
}
