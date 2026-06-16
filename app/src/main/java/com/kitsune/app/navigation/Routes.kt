package com.kitsune.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main") // Container for bottom nav screens
    
    // Bottom Nav Destinations
    object Bookmark : Screen("bookmark")
    object Playlist : Screen("playlist")
    object Local : Screen("local")
    object Other : Screen("other")
}
