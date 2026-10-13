package com.sloppydev.verta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sloppydev.verta.model.VideoItem
import com.sloppydev.verta.ui.components.VideoFeedOverlay
import com.sloppydev.verta.ui.components.VideoPlayerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable screen presenting a vertical feed of paginated, infinite randomized video items using [VerticalPager] and Paging 3.
 * Delegates item rendering to [VideoPlayerItem] and overlays to [VideoFeedOverlay].
 *
 * @param videoFlow The flow of paged [VideoItem] data.
 * @param onOpenSettings Callback invoked when the settings gear icon is long-pressed or change source is clicked.
 */
@Composable
fun VideoFeedScreen(
    videoFlow: Flow<PagingData<VideoItem>>,
    onOpenSettings: () -> Unit
) {
    val lazyPagingItems = videoFlow.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(pageCount = { lazyPagingItems.itemCount })
    var currentPosition by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        currentPosition = pagerState.currentPage
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (lazyPagingItems.itemCount == 0) {
            // Empty state view when no videos are found in source folder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No videos found in source folder, please add videos or change the source.",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onOpenSettings) {
                        Text(text = "Change Source")
                    }
                }
            }

            // Always provide settings button in top right corner even in empty state
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color(0x73000000), shape = RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { /* Normal click does nothing */ },
                            onLongPress = {
                                onOpenSettings()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val videoItem = lazyPagingItems[page] ?: return@VerticalPager
                val isActive = (page == pagerState.currentPage)

                var showHud by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()
                var hideHudJob by remember { mutableStateOf<Job?>(null) }

                Box(modifier = Modifier.fillMaxSize()) {
                    VideoPlayerItem(
                        videoItem = videoItem,
                        isActive = isActive,
                        onToggleHud = {
                            hideHudJob?.cancel()
                            showHud = true
                            hideHudJob = coroutineScope.launch {
                                delay(2000)
                                showHud = false
                            }
                        }
                    )

                    VideoFeedOverlay(
                        videoItem = videoItem,
                        currentIndex = page,
                        totalCount = lazyPagingItems.itemCount,
                        showHud = showHud,
                        onOpenSettings = onOpenSettings
                    )
                }
            }
        }
    }
}
