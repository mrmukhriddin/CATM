package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitDb
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetWorkPermitUseCase {

    operator fun invoke(id: Long, forceRefresh: Boolean): Single<WorkPermitDetailsX>
}

class GetWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
    private val workPermitDao: WorkPermitDao,
) : GetWorkPermitUseCase {

    override fun invoke(id: Long, forceRefresh: Boolean): Single<WorkPermitDetailsX> {
        return if (forceRefresh) {
            workPermitsApi.generateWorkPermitPdf(id)
                .andThen(workPermitsApi.getWorkPermitById(id))
                .flatMap { workPermitEnvelope ->
                    workPermitDao.insert(
                        WorkPermitDb(
                            workPermitEnvelope.mainInfo.id,
                            workPermitEnvelope
                        )
                    ).map { workPermitEnvelope }
                }
        } else {
            workPermitDao.getWorkPermitById(id).map {
                it.data
            }.switchIfEmpty(workPermitsApi.getWorkPermitById(id))
        }
    }
}
