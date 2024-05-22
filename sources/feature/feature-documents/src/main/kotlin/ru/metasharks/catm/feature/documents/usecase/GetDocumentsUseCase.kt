package ru.metasharks.catm.feature.documents.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.documents.entities.DocumentsEnvelopeX
import ru.metasharks.catm.feature.documents.services.DocumentsApi
import javax.inject.Inject

fun interface GetDocumentsUseCase {

    operator fun invoke(typeId: Int, nextUrl: String?): Single<DocumentsEnvelopeX>
}

internal class GetDocumentsUseCaseImpl @Inject constructor(
    private val documentsApi: DocumentsApi,
) : GetDocumentsUseCase {

    override fun invoke(typeId: Int, nextUrl: String?): Single<DocumentsEnvelopeX> {
        return if (nextUrl != null) {
            documentsApi.getDocuments(nextUrl)
        } else {
            documentsApi.getDocuments(typeId)
        }
    }
}
