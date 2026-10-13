package com.sloppydev.verta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
 * Composable screen displayed when no video folder has been selected yet.
 * Prompts the user to pick a folder containing video files.
 *
 * @param onPickFolder Callback invoked when the user clicks the pick folder button.
 */
@Composable
fun LandingScreen(onPickFolder: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
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
