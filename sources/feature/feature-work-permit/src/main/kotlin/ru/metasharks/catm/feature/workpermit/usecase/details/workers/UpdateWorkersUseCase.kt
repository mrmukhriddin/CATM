package ru.metasharks.catm.feature.workpermit.usecase.details.workers

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.entities.certain.workers.UpdateWorkersRequestX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface UpdateWorkersUseCase {

    operator fun invoke(workPermitId: Long, request: UpdateWorkersRequestX): Completable
}

internal class UpdateWorkersUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
) : UpdateWorkersUseCase {

    override fun invoke(workPermitId: Long, request: UpdateWorkersRequestX): Completable {
        return workPermitsApi.updateWorkers(workPermitId, request)
    }
}
