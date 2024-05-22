package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.entities.user.WorkerEnvelopeX
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface GetWorkersUseCase {

    operator fun invoke(nextPageUrl: String?): Single<WorkerEnvelopeX>
}

internal class GetWorkersUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
) : GetWorkersUseCase {

    override operator fun invoke(nextPageUrl: String?): Single<WorkerEnvelopeX> {
        return nextPageUrl?.let { profileApi.getWorkers(it) } ?: profileApi.getWorkers()
    }
}
