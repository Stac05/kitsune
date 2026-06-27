package com.kitsune.app.ui.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kitsune.app.domain.model.Comic
import com.kitsune.app.ui.library.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel,
    onComicClick: (Comic) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selectedPaths by viewModel.selectedPaths.collectAsState()

    val scope = rememberCoroutineScope()
    var isSearchActive by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showBulkRemoveConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { categories.size })
    val scrollStates = remember { mutableStateMapOf<Long, androidx.compose.foundation.lazy.grid.LazyGridState>() }

    LaunchedEffect(selectedCategoryId, categories) {
        val index = categories.indexOfFirst { it.id == selectedCategoryId }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (categories.isNotEmpty() && page < categories.size) {
                val categoryId = categories[page].id
                if (viewModel.selectedCategoryId.value != categoryId) {
                    viewModel.selectCategory(categoryId)
                    viewModel.clearSelection()
                }
            }
        }
    }

    BackHandler(enabled = selectionMode || isSearchActive) {
        if (selectionMode) {
            viewModel.clearSelection()
        } else {
            isSearchActive = false
            viewModel.onSearchQueryChange("")
        }
    }

    Scaffold(
        topBar = {
            Column {
                if (selectionMode) {
                    SelectionTopAppBar(
                        selectedCount = selectedPaths.size,
                        onCancel = { viewModel.clearSelection() },
                        onSelectAll = { viewModel.selectAll() },
                        actions = listOf(
                            SelectionAction(
                                icon = Icons.Default.Delete,
                                label = "Remove from Playlist",
                                onClick = { showBulkRemoveConfirm = true }
                            )
                        )
                    )
                } else if (isSearchActive) {
                    SearchTopAppBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onCloseClick = {
                            isSearchActive = false
                            viewModel.onSearchQueryChange("")
                        }
                    )
                } else {
                    TopAppBar(
                        title = { Text("Playlists") },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            if (selectedCategoryId != null) {
                                Box {
                                    IconButton(onClick = { showMenu = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Rename Playlist") },
                                            onClick = {
                                                showMenu = false
                                                showRenameDialog = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete Playlist") },
                                            onClick = {
                                                showMenu = false
                                                showDeleteConfirm = true
                                            },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                            colors = MenuDefaults.itemColors(
                                                textColor = MaterialTheme.colorScheme.error,
                                                leadingIconColor = MaterialTheme.colorScheme.error
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        )
                    )
                }

                if (!selectionMode && categories.isNotEmpty()) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Black,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 16.dp,
                        divider = {}
                    ) {
                        categories.forEachIndexed { index, category ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(category.name) }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!selectionMode && !isSearchActive) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Playlist")
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (categories.isEmpty()) {
                EmptyLibraryState(
                    message = "No playlists yet. Click + to create one.",
                    icon = Icons.AutoMirrored.Filled.List
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { if (it < categories.size) categories[it].id else it }
                ) { page ->
                    if (page == pagerState.currentPage) {
                        when (val state = uiState) {
                            is PlaylistUiState.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                            is PlaylistUiState.Empty -> {
                                EmptyLibraryState(
                                    message = if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\"" else "No comics in this playlist",
                                    icon = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.AutoMirrored.Filled.List
                                )
                            }
                            is PlaylistUiState.Error -> {
                                EmptyLibraryState(message = state.message, icon = Icons.Default.Error)
                            }
                            is PlaylistUiState.Success -> {
                                val categoryId = categories[page].id
                                val gridState = scrollStates.getOrPut(categoryId) { rememberLazyGridState() }
                                
                                ComicGrid(
                                    comics = state.comics,
                                    gridSize = state.gridSize,
                                    comicStatuses = state.comicStatuses,
                                    selectedPaths = selectedPaths,
                                    state = gridState,
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
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Playlist") },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Enter name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createPlaylist(name)
                        showAddDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showRenameDialog && selectedCategoryId != null) {
        val currentCategory = categories.find { it.id == selectedCategoryId }
        var newName by remember(currentCategory) { mutableStateOf(currentCategory?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Playlist") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        viewModel.renamePlaylist(selectedCategoryId!!, newName)
                        showRenameDialog = false
                    }
                }) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteConfirm && selectedCategoryId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Playlist") },
            text = { Text("Are you sure you want to delete this playlist? Comics will not be deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlaylist(selectedCategoryId!!)
                    showDeleteConfirm = false
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showBulkRemoveConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkRemoveConfirm = false },
            title = { Text("Remove Comics") },
            text = { Text("Remove ${selectedPaths.size} comics from this playlist?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeSelected()
                    showBulkRemoveConfirm = false
                }) { Text("Remove", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showBulkRemoveConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
