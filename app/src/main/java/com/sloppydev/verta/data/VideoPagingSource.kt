package com.sloppydev.verta.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sloppydev.verta.model.VideoItem

/**
 * A [PagingSource] that supplies an infinite, randomized sequence of [VideoItem]s
 * by continuously shuffling the master list of videos on each load request.
 *
 * @property allVideos The master list of all discovered videos in the selected folder.
 */
class VideoPagingSource(
    private val allVideos: List<VideoItem>
) : PagingSource<Int, VideoItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoItem> {
        if (allVideos.isEmpty()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        val page = params.key ?: 0
        // Shuffle a batch for this page to ensure infinite randomized playback
        val shuffledBatch = allVideos.shuffled()

        return LoadResult.Page(
            data = shuffledBatch,
            prevKey = if (page == 0) null else page - 1,
            nextKey = page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, VideoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
