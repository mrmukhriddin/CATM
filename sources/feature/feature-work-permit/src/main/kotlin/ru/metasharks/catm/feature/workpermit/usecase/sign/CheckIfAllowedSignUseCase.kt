package ru.metasharks.catm.feature.workpermit.usecase.sign

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface CheckIfAllowedSignUseCase {

    operator fun invoke(workPermitId: Long, role: String): Single<Boolean>
}

internal class CheckIfAllowedSignUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : CheckIfAllowedSignUseCase {

    override fun invoke(workPermitId: Long, role: String): Single<Boolean> {
        return workPermitsApi.isAllowedSignWorkPermit(workPermitId, role)
            .toSingle { true }.onErrorReturn { false }
    }
}
