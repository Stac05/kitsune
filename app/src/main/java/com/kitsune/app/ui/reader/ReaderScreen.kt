package com.kitsune.app.ui.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kitsune.app.reader.CbzPageModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Layar utama Reader untuk membaca komik.
 * Mendukung mode Vertical, LTR, dan RTL.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val chapterUri = viewModel.chapterUri
    val currentPage by viewModel.currentPage.collectAsState()

    Scaffold(
        topBar = {
            if (uiState is ReaderUiState.Success) {
                TopAppBar(
                    title = {
                        Text(
                            text = (uiState as ReaderUiState.Success).chapterName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ReaderUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ReaderUiState.Empty -> {
                    Text(
                        text = "No pages found in this chapter",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ReaderUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onBackClick) {
                            Text("Go Back")
                        }
                    }
                }
                is ReaderUiState.Success -> {
                    if (chapterUri != null) {
                        // Gunakan mode baca yang tersimpan di state
                        when (state.readingMode) {
                            "LTR" -> {
                                HorizontalReader(
                                    state = state,
                                    chapterUri = chapterUri,
                                    initialPage = currentPage,
                                    isRtl = false,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) }
                                )
                            }
                            "RTL" -> {
                                HorizontalReader(
                                    state = state,
                                    chapterUri = chapterUri,
                                    initialPage = currentPage,
                                    isRtl = true,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) }
                                )
                            }
                            else -> {
                                VerticalReader(
                                    state = state,
                                    chapterUri = chapterUri,
                                    initialPage = currentPage,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalReader(
    state: ReaderUiState.Success,
    chapterUri: android.net.Uri,
    initialPage: Int,
    onPageChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (initialPage - 1).coerceAtLeast(0)
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it + 1 }
            .distinctUntilChanged()
            .collect { onPageChange(it) }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = state.pages,
            key = { _, page -> page.entryPath }
        ) { _, page ->
            ReaderPage(
                chapterUri = chapterUri,
                entryPath = page.entryPath,
                isVertical = true
            )
        }
    }
}

@Composable
fun HorizontalReader(
    state: ReaderUiState.Success,
    chapterUri: android.net.Uri,
    initialPage: Int,
    isRtl: Boolean,
    onPageChange: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = (initialPage - 1).coerceAtLeast(0),
        pageCount = { state.pages.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .map { it + 1 }
            .distinctUntilChanged()
            .collect { onPageChange(it) }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = state.pages[pageIndex]
            ReaderPage(
                chapterUri = chapterUri,
                entryPath = page.entryPath,
                isVertical = false
            )
        }
    }
}

@Composable
fun ReaderPage(
    chapterUri: android.net.Uri,
    entryPath: String,
    isVertical: Boolean
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(CbzPageModel(chapterUri, entryPath))
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = if (isVertical) {
            Modifier.fillMaxWidth().wrapContentHeight()
        } else {
            Modifier.fillMaxSize()
        },
        contentScale = if (isVertical) ContentScale.FillWidth else ContentScale.Fit
    )
}
