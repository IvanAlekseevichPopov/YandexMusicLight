package com.example.yandexmusic.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen("home", "Главная", Icons.Default.Home)
    data object Search : Screen("search", "Поиск", Icons.Default.Search)
    data object Library : Screen("library", "Моя музыка", Icons.Default.LibraryMusic)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Library
)
