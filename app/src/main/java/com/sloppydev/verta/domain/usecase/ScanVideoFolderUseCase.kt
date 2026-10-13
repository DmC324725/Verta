package com.sloppydev.verta.domain.usecase

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.sloppydev.verta.model.VideoItem

/**
 * Use case responsible for recursively scanning a directory tree URI for video files.
 */
class ScanVideoFolderUseCase(private val context: Context) {

    /**
     * Recursively scans the given [treeUri] and collects all nested video [VideoItem]s.
     *
     * @param treeUri The root tree URI to scan.
     * @return List of discovered [VideoItem]s.
     */
    operator fun invoke(treeUri: Uri): List<VideoItem> {
        val tree = try {
            DocumentFile.fromTreeUri(context, treeUri)
        } catch (_: Exception) {
            null
        }

        val loadedVideos = mutableListOf<VideoItem>()
        if (tree != null && tree.exists()) {
            collectVideosRecursively(tree, loadedVideos)
        }
        return loadedVideos
    }

    private fun collectVideosRecursively(documentFile: DocumentFile, videoList: MutableList<VideoItem>) {
        try {
            if (!documentFile.exists()) return
            if (documentFile.isDirectory) {
                documentFile.listFiles()?.forEach { child ->
                    collectVideosRecursively(child, videoList)
                }
            } else if (documentFile.isFile && documentFile.type?.startsWith("video/") == true) {
                videoList.add(
                    VideoItem(
                        name = documentFile.name ?: "Unknown",
                        uri = documentFile.uri
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore unreadable files/folders gracefully to prevent crashes
        }
    }
}
