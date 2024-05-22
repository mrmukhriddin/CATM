package ru.metasharks.catm.feature.workpermit.usecase.details.workers

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface SignAllWorkersBriefingUseCase {

    operator fun invoke(briefingId: Long): Completable
}

internal class SignAllWorkersBriefingUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : SignAllWorkersBriefingUseCase {

    override fun invoke(briefingId: Long): Completable {
        return workPermitsApi.signWorkersBriefing(briefingId)
    }
}
