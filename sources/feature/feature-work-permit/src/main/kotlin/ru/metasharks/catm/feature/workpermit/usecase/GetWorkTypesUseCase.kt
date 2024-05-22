package ru.metasharks.catm.feature.workpermit.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.entities.worktype.WorkTypeX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface GetWorkTypesUseCase {

    operator fun invoke(): Single<List<WorkTypeX>>
}

class GetWorkTypesUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi
) : GetWorkTypesUseCase {

    override fun invoke(): Single<List<WorkTypeX>> {
        return workPermitsApi.getWorkTypes()
    }
}
