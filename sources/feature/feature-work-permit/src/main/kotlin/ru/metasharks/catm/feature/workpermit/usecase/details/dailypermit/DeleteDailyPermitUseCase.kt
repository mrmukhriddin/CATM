package ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface DeleteDailyPermitUseCase {

    operator fun invoke(workPermitId: Long, dailyPermitId: Long): Completable
}

internal class DeleteDailyPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : DeleteDailyPermitUseCase {

    override fun invoke(workPermitId: Long, dailyPermitId: Long): Completable {
        return workPermitsApi.deleteDailyPermit(workPermitId, dailyPermitId)
    }
}
