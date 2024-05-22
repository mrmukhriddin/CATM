package ru.metasharks.catm.feature.profile.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.flowable
import io.reactivex.rxjava3.core.Flowable
import ru.metasharks.catm.feature.profile.WorkersPageSource
import ru.metasharks.catm.feature.profile.entities.user.WorkerX
import javax.inject.Inject

fun interface GetWorkersPagingUseCase {

    operator fun invoke(url: String?): Flowable<PagingData<WorkerX>>
}

internal class GetWorkersPagingUseCaseImpl @Inject constructor(
    private val workersPageSource: WorkersPageSource,
) : GetWorkersPagingUseCase {

    override fun invoke(url: String?): Flowable<PagingData<WorkerX>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = { workersPageSource }
        ).flowable
    }
}
