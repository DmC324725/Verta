package com.sloppydev.verta.model

import android.net.Uri

/**
 * Represents a playable video item containing its display name and content URI.
 *
 * @property name The display name of the video file.
 * @property uri The content URI to load the video from.
 */
data class VideoItem(
    val name: String,
    val uri: Uri
)
