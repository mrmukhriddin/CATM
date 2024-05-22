package ru.metasharks.catm.feature.workpermit.usecase.details.dailypermit

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface SignDailyPermitUseCase {

    operator fun invoke(
        workPermitId: Long,
        dailyPermitId: Long,
        role: String,
        dateEnd: String?
    ): Completable
}

internal class SignDailyPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : SignDailyPermitUseCase {

    override fun invoke(
        workPermitId: Long,
        dailyPermitId: Long,
        role: String,
        dateEnd: String?
    ): Completable {
        return when (role) {
            SignerRole.PERMITTER.code -> workPermitsApi.signDailyPermit(
                workPermitId,
                dailyPermitId,
                role
            )
            SignerRole.RESPONSIBLE_MANAGER.code -> workPermitsApi.signDailyPermit(
                workPermitId,
                dailyPermitId,
                role,
                requireNotNull(dateEnd),
            )
            else -> throw IllegalArgumentException("Role should be whether permitter or responsible_manager")
        }
    }
}
