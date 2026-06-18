package com.kitsune.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main") // Container for bottom nav screens
    
    // Bottom Nav Destinations
    object Bookmark : Screen("bookmark")
    object Playlist : Screen("playlist")
    object Local : Screen("local")
    object Other : Screen("other")

    // Library Screens
    object ComicLibrary : Screen("comic_library")
    object VideoLibrary : Screen("video_library")

    // Detail Screens
    object ComicDetail : Screen("comic_detail/{comicRelativePath}") {
        fun createRoute(comicRelativePath: String) = "comic_detail/$comicRelativePath"
    }
}
