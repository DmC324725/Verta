package com.sloppydev.verta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sloppydev.verta.R
import com.sloppydev.verta.model.VideoItem

/**
 * Composable rendering the heads-up display (HUD) overlay (video title and index counter at the bottom)
 * and the settings gear button in the top right corner.
 *
 * @param videoItem The currently playing [VideoItem].
 * @param currentIndex The 0-based index of the current video.
 * @param totalCount The total item count in the paging queue.
 * @param showHud Whether the overlays are currently visible.
 * @param onOpenSettings Callback invoked when the settings gear icon is long-pressed.
 */
@Composable
fun VideoFeedOverlay(
    videoItem: VideoItem,
    currentIndex: Int,
    totalCount: Int,
    showHud: Boolean,
    onOpenSettings: () -> Unit
) {
    if (showHud) {
        // Floating HUD overlay at bottom showing video title and index counter
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight(Alignment.Bottom)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = videoItem.name,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.video_counter_format, currentIndex + 1, totalCount),
                color = Color(0xFFAAAAAA),
                fontSize = 12.sp
            )
        }

        // Settings button in top right corner (long-click opens settings)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
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
    }
}
