package com.kitsune.app.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicLibraryScreen(
    viewModel: LibraryViewModel,
    onComicClick: (Comic) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedPaths by viewModel.selectedPaths.collectAsState()
    
    val allBookmarks by viewModel.allBookmarks.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    
    // Picker Visibility
    var showBookmarkPicker by remember { mutableStateOf(false) }
    var showPlaylistPicker by remember { mutableStateOf(false) }
    
    // Create Category Visibility
    var showCreateBookmarkDialog by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    // Selection States for Dialogs
    var selectedBookmarkIds by remember { mutableStateOf(setOf<Long>()) }
    var selectedPlaylistIds by remember { mutableStateOf(setOf<Long>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Reset picker selections when opening pickers
    LaunchedEffect(showBookmarkPicker) {
        if (showBookmarkPicker) selectedBookmarkIds = emptySet()
    }
    LaunchedEffect(showPlaylistPicker) {
        if (showPlaylistPicker) selectedPlaylistIds = emptySet()
    }

    // OPTIMIZATION: Remember selection actions to prevent redundant re-allocation
    val selectionActions = remember {
        listOf(
            SelectionAction(
                icon = Icons.Default.BookmarkAdd,
                label = "Add to Bookmark",
                onClick = { showBookmarkPicker = true }
            ),
            SelectionAction(
                icon = Icons.AutoMirrored.Filled.List,
                label = "Add to Playlist",
                onClick = { showPlaylistPicker = true }
            )
        )
    }

    // OPTIMIZATION: Stable handlers for SearchTopAppBar
    val onSearchQueryChange = remember(viewModel) { { query: String -> viewModel.onSearchQueryChange(query) } }
    val onCloseSearch = remember(viewModel) {
        {
            isSearchActive = false
            viewModel.onSearchQueryChange("")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (selectionMode) {
                SelectionTopAppBar(
                    selectedCount = selectedPaths.size,
                    onCancel = { viewModel.clearSelection() },
                    onSelectAll = { viewModel.selectAll() },
                    actions = selectionActions
                )
            } else if (isSearchActive) {
                SearchTopAppBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onCloseClick = onCloseSearch
                )
            } else {
                TopAppBar(
                    title = { Text("Comic Library") },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is LibraryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LibraryUiState.Empty -> {
                    EmptyLibraryState(
                        message = if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\"" else "No Comics Found",
                        icon = Icons.Default.SearchOff
                    )
                }
                is LibraryUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshLibrary() }) {
                            Text("Retry")
                        }
                    }
                }
                is LibraryUiState.Success -> {
                    ComicGrid(
                        comics = state.comics,
                        gridSize = state.gridSize,
                        comicStatuses = state.comicStatuses,
                        selectedPaths = selectedPaths,
                        onComicClick = { comic ->
                            if (selectionMode) {
                                viewModel.toggleSelection(comic.relativePath)
                            } else {
                                onComicClick(comic)
                            }
                        },
                        onComicLongClick = { comic ->
                            viewModel.toggleSelection(comic.relativePath)
                        }
                    )
                }
            }
        }
    }

    // Generic Collection Pickers
    if (showBookmarkPicker) {
        // OPTIMIZATION: Remember mapped collections
        val bookmarkCollections = remember(allBookmarks) { allBookmarks.map { it.id to it.name } }
        CollectionPickerDialog(
            title = "Add to Bookmark",
            collections = bookmarkCollections,
            selectedIds = selectedBookmarkIds,
            onSelectionChanged = { selectedBookmarkIds = it },
            onConfirm = {
                viewModel.addSelectedToBookmarks(selectedBookmarkIds.toList())
                showBookmarkPicker = false
            },
            onDismiss = { showBookmarkPicker = false },
            onCreateNew = { showCreateBookmarkDialog = true }
        )
    }

    if (showPlaylistPicker) {
        // OPTIMIZATION: Remember mapped collections
        val playlistCollections = remember(allPlaylists) { allPlaylists.map { it.id to it.name } }
        CollectionPickerDialog(
            title = "Add to Playlist",
            collections = playlistCollections,
            selectedIds = selectedPlaylistIds,
            onSelectionChanged = { selectedPlaylistIds = it },
            onConfirm = {
                viewModel.addSelectedToPlaylists(selectedPlaylistIds.toList())
                showPlaylistPicker = false
            },
            onDismiss = { showPlaylistPicker = false },
            onCreateNew = { showCreatePlaylistDialog = true }
        )
    }

    // Reusable Create Dialogs
    if (showCreateBookmarkDialog) {
        GenericCreateDialog(
            title = "New Bookmark Category",
            hint = "Category name",
            onConfirm = { name ->
                scope.launch {
                    val newId = viewModel.createBookmark(name)
                    selectedBookmarkIds = selectedBookmarkIds + newId
                    showCreateBookmarkDialog = false
                }
            },
            onDismiss = { showCreateBookmarkDialog = false }
        )
    }

    if (showCreatePlaylistDialog) {
        GenericCreateDialog(
            title = "New Playlist",
            hint = "Playlist name",
            onConfirm = { name ->
                scope.launch {
                    val newId = viewModel.createPlaylist(name)
                    selectedPlaylistIds = selectedPlaylistIds + newId
                    showCreatePlaylistDialog = false
                }
            },
            onDismiss = { showCreatePlaylistDialog = false }
        )
    }
}
