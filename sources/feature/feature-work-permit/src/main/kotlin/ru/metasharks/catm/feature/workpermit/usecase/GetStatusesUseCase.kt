package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetStatusesUseCase {

    operator fun invoke(fromDb: Boolean): Single<List<StatusX>>
}

class GetStatusesUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
    private val workPermitDao: WorkPermitDao,
) : GetStatusesUseCase {

    override fun invoke(fromDb: Boolean): Single<List<StatusX>> {
        return if (fromDb) {
            workPermitDao.getStatuses()
        } else {
            workPermitsApi.getStatuses().flatMap {
                workPermitDao.insert(it).andThen(Single.just(it))
            }
        }.map { it.statuses }
    }
}
