package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitsEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.filter.FilterData
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetWorkPermitsUseCase {

    operator fun invoke(
        status: String,
        nextPageUrl: String?,
        filterData: FilterData?,
    ): Single<WorkPermitsEnvelopeX>
}

class GetWorkPermitsUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : GetWorkPermitsUseCase {

    override fun invoke(
        status: String,
        nextPageUrl: String?,
        filterData: FilterData?,
    ): Single<WorkPermitsEnvelopeX> {
        return if (nextPageUrl != null) {
            workPermitsApi.getWorkPermitsByUrl(nextPageUrl)
        } else {
            workPermitsApi.getWorkPermits(
                status = if (status == StatusX.ALL) {
                    null
                } else {
                    status
                },
                createdAfter = filterData?.createdAfter?.toString(),
                createdBefore = filterData?.createdBefore?.toString(),
                responsibleManagerId = filterData?.responsibleManagerId,
                workTypeId = filterData?.workTypeId
            )
        }
    }
}
