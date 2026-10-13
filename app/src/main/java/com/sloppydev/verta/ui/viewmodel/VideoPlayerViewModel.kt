package com.sloppydev.verta.ui.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sloppydev.verta.domain.usecase.GetShuffledVideoFeedUseCase
import com.sloppydev.verta.domain.usecase.ManageFolderPersistenceUseCase
import com.sloppydev.verta.domain.usecase.ScanVideoFolderUseCase
import com.sloppydev.verta.model.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsible for coordinating use cases for folder scanning, persistence,
 * and providing the infinite shuffled video paging flow.
 */
class VideoPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val persistenceUseCase = ManageFolderPersistenceUseCase(application)
    private val scanFolderUseCase = ScanVideoFolderUseCase(application)
    private val getShuffledVideoFeedUseCase = GetShuffledVideoFeedUseCase()

    private val _hasSelectedFolder = mutableStateOf(persistenceUseCase.hasSavedFolder())
    /** Indicates whether a source folder has been selected by the user. */
    val hasSelectedFolder: State<Boolean> = _hasSelectedFolder

    private val _videoFlow = MutableStateFlow<Flow<PagingData<VideoItem>>>(
        getShuffledVideoFeedUseCase(emptyList()).cachedIn(viewModelScope)
    )
    /** Flow of paged, shuffled video items. */
    val videoFlow: StateFlow<Flow<PagingData<VideoItem>>> = _videoFlow.asStateFlow()

    init {
        // Automatically restore and load previously selected directory if permission is still valid
        persistenceUseCase.getSavedUriString()?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                if (persistenceUseCase.hasValidPermission(uri, application.contentResolver)) {
                    loadFolder(uri, application.contentResolver)
                } else {
                    persistenceUseCase.clearSavedFolder()
                    _hasSelectedFolder.value = true
                }
            } catch (_: Exception) {
                persistenceUseCase.clearSavedFolder()
                _hasSelectedFolder.value = true
            }
        }
    }

    /**
     * Loads videos from the specified tree URI using use cases, persists permission,
     * and initializes the video feed flow. If no videos are found, shows a Toast.
     *
     * @param treeUri The root tree URI selected via Storage Access Framework.
     * @param contentResolver The application content resolver used to take permissions.
     */
    fun loadFolder(treeUri: Uri, contentResolver: ContentResolver) {
        persistenceUseCase.saveFolderUri(treeUri, contentResolver)
        _hasSelectedFolder.value = true

        val loadedVideos = scanFolderUseCase(treeUri)

        val flow = getShuffledVideoFeedUseCase(loadedVideos).cachedIn(viewModelScope)
        _videoFlow.value = flow

        if (loadedVideos.isEmpty()) {
            showNoVideosToast()
        }
    }

    /**
     * Shows a "No videos found" toast message on the main thread.
     */
    private fun showNoVideosToast() {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                getApplication(),
                "No videos found in the selected folder",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
