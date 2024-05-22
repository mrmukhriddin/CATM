package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.entities.user.WorkerEnvelopeX
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface SearchWorkersUseCase {

    operator fun invoke(searchQuery: String, nextPageUrl: String?): Single<WorkerEnvelopeX>
}

class SearchWorkersUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
) : SearchWorkersUseCase {

    override fun invoke(searchQuery: String, nextPageUrl: String?): Single<WorkerEnvelopeX> {
        return nextPageUrl?.let {
            profileApi.getWorkers(it)
        } ?: profileApi.searchWorkers(searchQuery)
    }
}
