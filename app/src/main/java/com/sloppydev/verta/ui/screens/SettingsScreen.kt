package com.sloppydev.verta.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sloppydev.verta.R

/**
 * Composable settings screen allowing the user to change the video folder selection or go back
 * to the video feed with their previous selection.
 *
 * @param onPickFolder Callback to launch the document tree picker.
 * @param onBack Callback invoked when the user presses back or the back button.
 * @param showBack Whether to show the top back navigation icon (false if no folder has been selected yet).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onPickFolder: () -> Unit,
    onBack: () -> Unit,
    showBack: Boolean = true
) {
    if (showBack) {
        BackHandler {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (showBack) {
            TopAppBar(
                title = {
                    Text(text = "Settings", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "Verta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.source_folder_subtitle),
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onPickFolder) {
                Text(text = stringResource(R.string.select_folder))
            }
        }
    }
}
