package com.kitsune.app.ui.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kitsune.app.reader.CbzPageModel

/**
 * Layar utama Reader untuk membaca komik.
 * Menampilkan halaman secara vertikal (Vertical Reading).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val chapterUri = viewModel.chapterUri

    Scaffold(
        topBar = {
            if (uiState is ReaderUiState.Success) {
                TopAppBar(
                    title = {
                        Text(
                            text = (uiState as ReaderUiState.Success).chapterName,
                            style = MaterialTheme.typography.titleMedium
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.pages, key = { it.entryPath }) { page ->
                                ReaderPage(
                                    chapterUri = chapterUri,
                                    entryPath = page.entryPath
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
fun ReaderPage(
    chapterUri: android.net.Uri,
    entryPath: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(CbzPageModel(chapterUri, entryPath))
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentScale = ContentScale.FillWidth
    )
}
