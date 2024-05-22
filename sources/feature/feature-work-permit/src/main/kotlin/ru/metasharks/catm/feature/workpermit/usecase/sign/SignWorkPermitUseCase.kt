package ru.metasharks.catm.feature.workpermit.usecase.sign

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface SignWorkPermitUseCase {

    operator fun invoke(workPermitId: Long, role: String, sign: Boolean): Completable
}

internal class SignWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : SignWorkPermitUseCase {

    override fun invoke(workPermitId: Long, role: String, sign: Boolean): Completable {
        return workPermitsApi.signWorkPermit(workPermitId, role, sign)
    }
}
