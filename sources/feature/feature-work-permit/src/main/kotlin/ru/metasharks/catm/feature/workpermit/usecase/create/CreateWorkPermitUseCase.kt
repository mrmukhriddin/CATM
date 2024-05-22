package ru.metasharks.catm.feature.workpermit.usecase.create

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitInfo
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitResponseX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import javax.inject.Inject

fun interface CreateWorkPermitUseCase {

    operator fun invoke(createWorkPermitInfo: CreateWorkPermitInfo): Single<CreateWorkPermitResponseX>
}

class CreateWorkPermitUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
    private val fileManager: FileManager,
) : CreateWorkPermitUseCase {

    override fun invoke(createWorkPermitInfo: CreateWorkPermitInfo): Single<CreateWorkPermitResponseX> {
        val body = CreateWorkPermitBuilder(fileManager)
            .putCreateInfo(createWorkPermitInfo)
            .build()
        return workPermitsApi.createWorkPermit(body.parts)
    }
}
