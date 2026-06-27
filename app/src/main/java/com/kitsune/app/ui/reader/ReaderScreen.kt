package com.kitsune.app.ui.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kitsune.app.reader.CbzPageModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Layar utama Reader untuk membaca komik.
 * Mendukung mode Vertical, LTR, dan RTL dengan navigasi chapter otomatis,
 * Page Slider, dan Overlay Controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val transitionState by viewModel.transitionState.collectAsState()
    val chapterUri by viewModel.chapterUri.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    
    var showControls by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // Konten Reader (Gambar)
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
                    // key(chapterUri) memastikan state scroll/pager di-reset saat pindah chapter
                    key(chapterUri) {
                        when (state.readingMode) {
                            "LTR" -> {
                                HorizontalReader(
                                    state = state,
                                    chapterUri = chapterUri!!,
                                    initialPage = currentPage,
                                    isRtl = false,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) },
                                    onNextChapter = { viewModel.navigateToNextChapter() },
                                    onPrevChapter = { viewModel.navigateToPreviousChapter() },
                                    hasNext = viewModel.hasNextChapter(),
                                    hasPrev = viewModel.hasPreviousChapter()
                                )
                            }
                            "RTL" -> {
                                HorizontalReader(
                                    state = state,
                                    chapterUri = chapterUri!!,
                                    initialPage = currentPage,
                                    isRtl = true,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) },
                                    onNextChapter = { viewModel.navigateToNextChapter() },
                                    onPrevChapter = { viewModel.navigateToPreviousChapter() },
                                    hasNext = viewModel.hasNextChapter(),
                                    hasPrev = viewModel.hasPreviousChapter()
                                )
                            }
                            else -> {
                                VerticalReader(
                                    state = state,
                                    chapterUri = chapterUri!!,
                                    initialPage = currentPage,
                                    onPageChange = { viewModel.saveProgress(it, state.pages.size) },
                                    onNextChapter = { viewModel.navigateToNextChapter() },
                                    hasNext = viewModel.hasNextChapter()
                                )
                            }
                        }
                    }
                }
            }
        }

        // REVISION 2: Chapter Transition Overlay
        AnimatedVisibility(
            visible = transitionState.isTransitioning,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 1.1f),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = transitionState.chapterTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Loading chapter...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Overlay Top Bar
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                TopAppBar(
                    title = {
                        if (uiState is ReaderUiState.Success) {
                            Text(
                                text = (uiState as ReaderUiState.Success).chapterName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
            }
        }

        // Overlay Bottom Bar (Slider + Nav)
        AnimatedVisibility(
            visible = showControls,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReaderBottomBar(
                currentPage = currentPage,
                totalPages = (uiState as? ReaderUiState.Success)?.pages?.size ?: 1,
                hasNext = viewModel.hasNextChapter(),
                hasPrev = viewModel.hasPreviousChapter(),
                onPageJump = { viewModel.jumpToPage(it) },
                onNextChapter = { viewModel.navigateToNextChapter() },
                onPrevChapter = { viewModel.navigateToPreviousChapter() }
            )
        }
    }
}

@Composable
fun VerticalReader(
    state: ReaderUiState.Success,
    chapterUri: android.net.Uri,
    initialPage: Int,
    onPageChange: (Int) -> Unit,
    onNextChapter: () -> Unit,
    hasNext: Boolean
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (initialPage - 1).coerceAtLeast(0)
    )

    // Jump to page when initialPage changes (Slider sync)
    LaunchedEffect(initialPage) {
        val targetIndex = (initialPage - 1).coerceAtLeast(0)
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

    // Save Progress (UI -> VM sync)
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it + 1 }
            .distinctUntilChanged()
            .collect { 
                if (it <= state.pages.size) {
                    onPageChange(it)
                }
            }
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
        
        if (hasNext) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { onNextChapter() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tap to load Next Chapter",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalReader(
    state: ReaderUiState.Success,
    chapterUri: android.net.Uri,
    initialPage: Int,
    isRtl: Boolean,
    onPageChange: (Int) -> Unit,
    onNextChapter: () -> Unit,
    onPrevChapter: () -> Unit,
    hasNext: Boolean,
    hasPrev: Boolean
) {
    val actualPageCount = state.pages.size
    val totalCount = actualPageCount + (if (hasPrev) 1 else 0) + (if (hasNext) 1 else 0)
    val startIndex = if (hasPrev) (initialPage) else (initialPage - 1)

    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, totalCount - 1),
        pageCount = { totalCount }
    )

    // Jump to page when initialPage changes (Slider sync)
    LaunchedEffect(initialPage) {
        val targetIndex = if (hasPrev) initialPage else initialPage - 1
        if (pagerState.currentPage != targetIndex) {
            pagerState.scrollToPage(targetIndex.coerceIn(0, totalCount - 1))
        }
    }

    // Sync Page and Save Progress + Auto Transition
    LaunchedEffect(pagerState.currentPage) {
        val currentIdx = pagerState.currentPage
        when {
            hasPrev && currentIdx == 0 -> onPrevChapter()
            hasNext && currentIdx == totalCount - 1 -> onNextChapter()
            else -> {
                val realPage = if (hasPrev) currentIdx else currentIdx + 1
                if (realPage in 1..actualPageCount) {
                    onPageChange(realPage)
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { index ->
            when {
                hasPrev && index == 0 -> {
                    ChapterTransitionPage(message = "Loading Previous Chapter...")
                }
                hasNext && index == totalCount - 1 -> {
                    ChapterTransitionPage(message = "Loading Next Chapter...")
                }
                else -> {
                    val pageIdx = if (hasPrev) index - 1 else index
                    if (pageIdx in state.pages.indices) {
                        val page = state.pages[pageIdx]
                        ReaderPage(
                            chapterUri = chapterUri,
                            entryPath = page.entryPath,
                            isVertical = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterTransitionPage(message: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.Gray, fontWeight = FontWeight.Bold)
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

@Composable
fun ReaderBottomBar(
    currentPage: Int,
    totalPages: Int,
    hasNext: Boolean,
    hasPrev: Boolean,
    onPageJump: (Int) -> Unit,
    onNextChapter: () -> Unit,
    onPrevChapter: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.8f),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentPage / $totalPages",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(56.dp)
                )
                
                Slider(
                    value = currentPage.toFloat(),
                    onValueChange = { onPageJump(it.toInt()) },
                    valueRange = 1f..totalPages.toFloat().coerceAtLeast(1f),
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevChapter, enabled = hasPrev) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev Chapter")
                }
                
                Text(
                    text = "Chapter Navigation",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                
                IconButton(onClick = onNextChapter, enabled = hasNext) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Chapter")
                }
            }
        }
    }
}
