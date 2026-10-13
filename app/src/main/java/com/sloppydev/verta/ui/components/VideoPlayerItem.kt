package com.sloppydev.verta.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.sloppydev.verta.model.VideoItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composable rendering an individual video player page within the vertical feed,
 * handling ExoPlayer lifecycle, playback progress, touch gestures (tap-to-pause and 2x speed hold),
 * and visual feedback overlays (play/pause icon and speed badge).
 *
 * @param videoItem The [VideoItem] to play.
 * @param isActive Whether this video page is currently active/selected in the pager.
 * @param onToggleHud Callback to toggle HUD and settings button visibility.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerItem(
    videoItem: VideoItem,
    isActive: Boolean,
    onToggleHud: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(videoItem.uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoItem.uri))
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var showIcon by remember { mutableStateOf(false) }
    var iconRes by remember { mutableIntStateOf(android.R.drawable.ic_media_pause) }
    var isSpeedMode by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()
    var speedJob by remember { mutableStateOf<Job?>(null) }
    var hideIconJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Observe app lifecycle to pause video on background and resume on foreground
    DisposableEffect(lifecycleOwner, exoPlayer, isActive) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (isActive) {
                        exoPlayer.play()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
            exoPlayer.playbackParameters = PlaybackParameters(1f)
            isSpeedMode = false
        }
    }

    // Progress ticker coroutine
    LaunchedEffect(isActive, isPlaying) {
        if (isActive && isPlaying) {
            while (true) {
                val duration = exoPlayer.duration
                if (duration > 0) {
                    progress = exoPlayer.currentPosition.toFloat() / duration.toFloat()
                }
                delay(200)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(exoPlayer) {
                detectTapGestures(
                    onTap = {
                        hideIconJob?.cancel()
                        onToggleHud()
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                            iconRes = android.R.drawable.ic_media_play
                            showIcon = true
                        } else {
                            exoPlayer.play()
                            iconRes = android.R.drawable.ic_media_pause
                            showIcon = true
                            hideIconJob = coroutineScope.launch {
                                delay(400)
                                showIcon = false
                            }
                        }
                    }
                )
            }
            .pointerInput(exoPlayer) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        val x = change.position.x
                        val width = size.width

                        when (event.type) {
                            PointerEventType.Press -> {
                                isSpeedMode = false
                                if (x > width * 0.67f) {
                                    speedJob?.cancel()
                                    speedJob = coroutineScope.launch {
                                        delay(300)
                                        isSpeedMode = true
                                        exoPlayer.playbackParameters = PlaybackParameters(2f)
                                    }
                                }
                            }
                            PointerEventType.Release, PointerEventType.Exit -> {
                                speedJob?.cancel()
                                speedJob = null
                                if (isSpeedMode) {
                                    exoPlayer.playbackParameters = PlaybackParameters(1f)
                                    isSpeedMode = false
                                }
                            }
                        }
                    }
                }
            }
    ) {
        ContentFrame(
            player = exoPlayer,
            modifier = Modifier.fillMaxSize()
        )

        // Play/Pause state feedback overlay icon
        if (showIcon) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
                    .background(Color(0x73000000), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // 2x Speed indicator overlay
        if (isSpeedMode) {
            Text(
                text = "2x",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0x55FFFFFF),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom playback progress bar track & fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x33FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(2.dp)
                    .background(Color(0x88FFFFFF))
            )
        }
    }
}
