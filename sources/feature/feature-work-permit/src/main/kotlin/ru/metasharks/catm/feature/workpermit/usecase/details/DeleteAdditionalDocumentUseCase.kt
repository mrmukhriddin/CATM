package ru.metasharks.catm.feature.workpermit.usecase.details

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitDb
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import javax.inject.Inject

fun interface DeleteAdditionalDocumentUseCase {

    operator fun invoke(workPermitId: Long, docId: Long): Completable
}

internal class DeleteAdditionalDocumentUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
    private val workPermitDao: WorkPermitDao,
    private val getWorkPermitUseCase: GetWorkPermitUseCase,
) : DeleteAdditionalDocumentUseCase {

    override fun invoke(workPermitId: Long, docId: Long): Completable {
        return workPermitsApi.deleteAdditionalDocument(workPermitId, docId)
            .toSingle {}.flatMap {
                workPermitDao.getWorkPermitById(workPermitId).switchIfEmpty(
                    getWorkPermitUseCase(workPermitId, true).map {
                        WorkPermitDb(id = it.mainInfo.id, data = it)
                    }
                ).flatMap { wp ->
                    workPermitDao.insert(
                        wp.copy(
                            data = wp.data.copy(
                                documents = wp.data.documents.filter { it.id != docId }
                            )
                        )
                    )
                }
            }.flatMapCompletable { Completable.fromAction { } }
    }
}
