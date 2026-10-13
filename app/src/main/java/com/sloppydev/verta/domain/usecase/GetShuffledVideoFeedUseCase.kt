package com.sloppydev.verta.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sloppydev.verta.data.VideoPagingSource
import com.sloppydev.verta.model.VideoItem
import kotlinx.coroutines.flow.Flow

/**
 * Use case responsible for creating an infinite, randomized Paging 3 [Pager] flow from a list of videos.
 */
class GetShuffledVideoFeedUseCase {

    /**
     * Creates a paged flow that continuously shuffles and serves [VideoItem] batches.
     *
     * @param videos The master list of discovered videos.
     * @return A [Flow] of [PagingData] containing shuffled [VideoItem]s.
     */
    operator fun invoke(videos: List<VideoItem>): Flow<PagingData<VideoItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { VideoPagingSource(videos) }
        ).flow
    }
}
