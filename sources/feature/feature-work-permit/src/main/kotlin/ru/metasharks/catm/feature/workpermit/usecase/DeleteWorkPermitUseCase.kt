package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface DeleteWorkPermitUseCase {

    operator fun invoke(id: Long): Completable
}

class DeleteWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : DeleteWorkPermitUseCase {

    override fun invoke(id: Long): Completable {
        return workPermitsApi.deleteWorkPermit(id)
    }
}
