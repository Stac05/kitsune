package com.kitsune.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.database.AppDatabase
import com.kitsune.app.navigation.Screen
import com.kitsune.app.scanner.ComicScanner
import com.kitsune.app.ui.bookmark.BookmarkScreen
import com.kitsune.app.ui.comicdetail.ComicDetailScreen
import com.kitsune.app.ui.comicdetail.ComicDetailViewModel
import com.kitsune.app.ui.library.ComicLibraryScreen
import com.kitsune.app.ui.library.LibraryViewModel
import com.kitsune.app.ui.local.LocalScreen
import com.kitsune.app.ui.playlist.PlaylistScreen
import com.kitsune.app.ui.settings.OtherScreen
import com.kitsune.app.ui.splash.SplashScreen
import com.kitsune.app.ui.splash.SplashViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI for Phase 3.1
        val database = AppDatabase.getDatabase(this)
        val settingsRepository = SettingsRepository(database.settingsDao())
        val storageHelper = StorageHelper(this)
        val comicScanner = ComicScanner(this)
        val scannerRepository = ScannerRepository(comicScanner, database.comicDao())
        
        val splashViewModel = SplashViewModel(settingsRepository, storageHelper)
        val libraryViewModel = LibraryViewModel(scannerRepository, settingsRepository)

        enableEdgeToEdge()
        setContent {
            KitsuneTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = Screen.Splash.route) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            viewModel = splashViewModel,
                            storageHelper = storageHelper,
                            onNavigateToMain = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Main.route) {
                        MainContainer(
                            libraryViewModel = libraryViewModel,
                            scannerRepository = scannerRepository,
                            settingsRepository = settingsRepository
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContainer(
    libraryViewModel: LibraryViewModel,
    scannerRepository: ScannerRepository,
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("Bookmark", Screen.Bookmark.route, Icons.Default.Star),
        BottomNavItem("Playlist", Screen.Playlist.route, Icons.AutoMirrored.Filled.List),
        BottomNavItem("Local", Screen.Local.route, Icons.Default.Home),
        BottomNavItem("Other", Screen.Other.route, Icons.Default.Settings),
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val rootRoutes = items.map { it.route }
            val showBottomBar = currentDestination?.route in rootRoutes

            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Local.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Bookmark.route) { BookmarkScreen() }
            composable(Screen.Playlist.route) { PlaylistScreen() }
            composable(Screen.Local.route) { 
                LocalScreen(
                    onComicsClick = { navController.navigate(Screen.ComicLibrary.route) },
                    onVideosClick = { /* Future */ }
                ) 
            }
            composable(Screen.Other.route) { OtherScreen() }
            
            composable(Screen.ComicLibrary.route) {
                ComicLibraryScreen(
                    viewModel = libraryViewModel,
                    onComicClick = { comic ->
                        navController.navigate(Screen.ComicDetail.createRoute(comic.relativePath))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ComicDetail.route,
                arguments = listOf(navArgument("comicRelativePath") { type = NavType.StringType })
            ) { backStackEntry ->
                val comicRelativePath = backStackEntry.arguments?.getString("comicRelativePath") ?: ""
                val viewModel = ComicDetailViewModel(
                    comicRelativePath = comicRelativePath,
                    scannerRepository = scannerRepository,
                    settingsRepository = settingsRepository
                )
                ComicDetailScreen(
                    viewModel = viewModel,
                    onChapterClick = { chapter ->
                        // Future: Navigate to reader
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun KitsuneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF9800), // Orange Accent
            background = Color.Black,
            surface = Color(0xFF121212)
        ),
        content = content
    )
}
