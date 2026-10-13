package com.sloppydev.verta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sloppydev.verta.ui.screens.SettingsScreen
import com.sloppydev.verta.ui.screens.VideoFeedScreen
import com.sloppydev.verta.ui.theme.VertaTheme
import com.sloppydev.verta.ui.viewmodel.VideoPlayerViewModel

/**
 * Main application entry point for the Verta application.
 * Hosts the Jetpack Compose navigation flow between the landing folder-picker screen,
 * the settings screen, and the vertical video feed player screen.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: VideoPlayerViewModel by viewModels()
    private var showSettings by mutableStateOf(false)

    private val folderPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.loadFolder(it, contentResolver)
            showSettings = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VertaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val videoFlow = viewModel.videoFlow.collectAsState().value
                    if (showSettings) {
                        SettingsScreen(
                            onPickFolder = { folderPicker.launch(null) },
                            onBack = { showSettings = false }
                        )
                    } else {
                        VideoFeedScreen(
                            videoFlow = videoFlow,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
