package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitX
import javax.inject.Inject

fun interface GetWorkPermitsFromDbUseCase {

    operator fun invoke(): Single<List<WorkPermitX>>
}

class GetWorkPermitsFromDbUseCaseImpl @Inject constructor(
    private val currentUserUseCase: GetCurrentUserUseCase,
    private val workPermitDao: WorkPermitDao,
) : GetWorkPermitsFromDbUseCase {

    override fun invoke(): Single<List<WorkPermitX>> {
        return currentUserUseCase(initialLoad = false)
            .flatMap { user ->
                workPermitDao.getWorkPermitsMini(user.id).map { workPermitsMini ->
                    workPermitsMini.map { it.data }
                }
            }
    }
}
