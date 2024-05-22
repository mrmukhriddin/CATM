package ru.metasharks.catm.feature.workpermit.usecase.details.signed

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface CloseWorkPermitUseCase {

    operator fun invoke(workPermitId: Long): Completable
}

internal class CloseWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : CloseWorkPermitUseCase {

    override fun invoke(workPermitId: Long): Completable {
        return workPermitsApi.closeWorkPermit(workPermitId)
    }
}
