package ru.metasharks.catm.feature.workpermit.usecase.details

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.core.network.FileConverter
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitDb
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDocumentX
import ru.metasharks.catm.feature.workpermit.entities.certain.upload.UploadAdditionalDocumentResponseX
import ru.metasharks.catm.feature.workpermit.services.WorkPermitsApi
import ru.metasharks.catm.feature.workpermit.usecase.GetWorkPermitUseCase
import java.io.File
import javax.inject.Inject

fun interface UploadAdditionalDocumentUseCase {

    operator fun invoke(workPermitId: Long, file: File): Single<UploadAdditionalDocumentResponseX>
}

internal class UploadAdditionalDocumentUseCaseImpl @Inject constructor(
    private val workPermitsApi: WorkPermitsApi,
    private val workPermitDao: WorkPermitDao,
    private val getWorkPermitUseCase: GetWorkPermitUseCase,
) : UploadAdditionalDocumentUseCase {

    override fun invoke(workPermitId: Long, file: File): Single<UploadAdditionalDocumentResponseX> {
        val filePart = FileConverter.fileToMultipart(file, "file")
        return workPermitsApi.uploadAdditionalDocument(workPermitId, filePart).flatMap { response ->
            workPermitDao.getWorkPermitById(workPermitId).switchIfEmpty(
                getWorkPermitUseCase(workPermitId, true).map {
                    WorkPermitDb(id = it.mainInfo.id, data = it)
                }
            ).flatMap { wp ->
                workPermitDao.insert(
                    wp.copy(
                        data = wp.data.copy(
                            documents = wp.data.documents.plus(
                                WorkPermitDocumentX(
                                    fileUrl = response.fileUrl,
                                    fileName = response.fileName,
                                    id = response.id,
                                    workPermit = workPermitId,
                                    fileSize = response.fileSize,
                                )
                            )
                        )
                    )
                )
            }.map { response }
        }
    }
}
