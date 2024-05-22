package ru.metasharks.catm.feature.profile

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.entities.user.WorkerEnvelopeX
import ru.metasharks.catm.feature.profile.entities.user.WorkerX
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

class WorkersPageSource @Inject constructor(
    private val profileApi: ProfileApi,
) : RxPagingSource<String, WorkerX>() {

    override fun getRefreshKey(state: PagingState<String, WorkerX>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return null
    }

    override fun loadSingle(params: LoadParams<String>): Single<LoadResult<String, WorkerX>> {
        val key = params.key

        return if (key == null) {
            profileApi.getWorkers()
        } else {
            profileApi.getWorkers(key)
        }
            .map { toLoadResult(it) }
    }

    private fun toLoadResult(it: WorkerEnvelopeX): LoadResult<String, WorkerX> {
        return LoadResult.Page(
            data = it.workers,
            nextKey = it.next,
            prevKey = it.previous,
        )
    }
}
