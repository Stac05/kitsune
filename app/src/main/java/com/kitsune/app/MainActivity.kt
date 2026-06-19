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
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.BookmarkRepository
import com.kitsune.app.data.repository.BookmarkWithCount
import com.kitsune.app.data.repository.ReaderRepository
import com.kitsune.app.data.repository.ReadingProgressRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.database.AppDatabase
import com.kitsune.app.database.entity.ReadingProgressEntity
import com.kitsune.app.navigation.Screen
import com.kitsune.app.reader.CbzImageFetcher
import com.kitsune.app.reader.CbzParser
import com.kitsune.app.scanner.ComicScanner
import com.kitsune.app.ui.bookmark.BookmarkDetailScreen
import com.kitsune.app.ui.bookmark.BookmarkDetailViewModel
import com.kitsune.app.ui.bookmark.BookmarkScreen
import com.kitsune.app.ui.bookmark.BookmarkViewModel
import com.kitsune.app.ui.comicdetail.ComicDetailScreen
import com.kitsune.app.ui.comicdetail.ComicDetailViewModel
import com.kitsune.app.ui.library.ComicLibraryScreen
import com.kitsune.app.ui.library.LibraryViewModel
import com.kitsune.app.ui.local.LocalScreen
import com.kitsune.app.ui.playlist.PlaylistScreen
import com.kitsune.app.ui.reader.ReaderScreen
import com.kitsune.app.ui.reader.ReaderViewModel
import com.kitsune.app.ui.settings.OtherScreen
import com.kitsune.app.ui.splash.SplashScreen
import com.kitsune.app.ui.splash.SplashViewModel

class MainActivity : ComponentActivity(), ImageLoaderFactory {

    private lateinit var readerRepository: ReaderRepository
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var scannerRepository: ScannerRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var progressRepository: ReadingProgressRepository
    private lateinit var storageHelper: StorageHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI
        val database = AppDatabase.getDatabase(this)
        settingsRepository = SettingsRepository(database.settingsDao())
        storageHelper = StorageHelper(this)
        val comicScanner = ComicScanner(this)
        scannerRepository = ScannerRepository(comicScanner, database.comicDao())
        progressRepository = ReadingProgressRepository(database.readingProgressDao())
        bookmarkRepository = BookmarkRepository(database.bookmarkDao())
        
        val cbzParser = CbzParser(this)
        readerRepository = ReaderRepository(cbzParser)
        
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
                            settingsRepository = settingsRepository,
                            readerRepository = readerRepository,
                            progressRepository = progressRepository,
                            bookmarkRepository = bookmarkRepository,
                            storageHelper = storageHelper
                        )
                    }
                }
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(CbzImageFetcher.Factory(applicationContext, readerRepository))
            }
            .build()
    }
}

@Composable
fun MainContainer(
    libraryViewModel: LibraryViewModel,
    scannerRepository: ScannerRepository,
    settingsRepository: SettingsRepository,
    readerRepository: ReaderRepository,
    progressRepository: ReadingProgressRepository,
    bookmarkRepository: BookmarkRepository,
    storageHelper: StorageHelper
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
            composable(Screen.Bookmark.route) { 
                val viewModel = remember { BookmarkViewModel(bookmarkRepository, settingsRepository) }
                BookmarkScreen(
                    viewModel = viewModel,
                    onBookmarkClick = { item: BookmarkWithCount ->
                        navController.navigate(Screen.BookmarkDetail.createRoute(item.bookmark.id))
                    }
                ) 
            }
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
                val viewModel = remember(comicRelativePath) {
                    ComicDetailViewModel(
                        comicRelativePath = comicRelativePath,
                        scannerRepository = scannerRepository,
                        settingsRepository = settingsRepository,
                        progressRepository = progressRepository,
                        bookmarkRepository = bookmarkRepository
                    )
                }
                ComicDetailScreen(
                    viewModel = viewModel,
                    onChapterClick = { chapter ->
                        navController.navigate(Screen.Reader.createRoute(comicRelativePath, chapter.relativePath))
                    },
                    onContinueClick = { progress: ReadingProgressEntity ->
                        navController.navigate(Screen.Reader.createRoute(comicRelativePath, progress.chapterRelativePath))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Reader.route,
                arguments = listOf(
                    navArgument("comicRelativePath") { type = NavType.StringType },
                    navArgument("chapterRelativePath") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val comicRelativePath = backStackEntry.arguments?.getString("comicRelativePath") ?: ""
                val chapterRelativePath = backStackEntry.arguments?.getString("chapterRelativePath") ?: ""
                
                val viewModel = remember(comicRelativePath, chapterRelativePath) {
                    ReaderViewModel(
                        comicRelativePath = comicRelativePath,
                        currentChapterPath = chapterRelativePath,
                        readerRepository = readerRepository,
                        settingsRepository = settingsRepository,
                        progressRepository = progressRepository,
                        scannerRepository = scannerRepository,
                        storageHelper = storageHelper
                    )
                }
                
                ReaderScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.BookmarkDetail.route,
                arguments = listOf(navArgument("bookmarkId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookmarkId = backStackEntry.arguments?.getLong("bookmarkId") ?: 0L
                val viewModel = remember(bookmarkId) {
                    BookmarkDetailViewModel(
                        bookmarkId = bookmarkId,
                        bookmarkRepository = bookmarkRepository,
                        scannerRepository = scannerRepository,
                        settingsRepository = settingsRepository
                    )
                }
                BookmarkDetailScreen(
                    viewModel = viewModel,
                    onComicClick = { comic ->
                        navController.navigate(Screen.ComicDetail.createRoute(comic.relativePath))
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
