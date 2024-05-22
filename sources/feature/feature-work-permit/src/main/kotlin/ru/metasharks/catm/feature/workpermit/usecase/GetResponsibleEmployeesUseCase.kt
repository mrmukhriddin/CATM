package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUsersEnvelopeX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetResponsibleEmployeesUseCase {

    operator fun invoke(nextUrl: String?, query: String?): Single<WorkPermitUsersEnvelopeX>
}

class GetResponsibleEmployeesUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : GetResponsibleEmployeesUseCase {

    override fun invoke(nextUrl: String?, query: String?): Single<WorkPermitUsersEnvelopeX> {
        return if (nextUrl != null) {
            workPermitsApi.getResponsibleManagersByUrl(nextUrl)
        } else {
            workPermitsApi.getPickerListItems(query)
        }
    }
}
