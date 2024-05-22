package ru.metasharks.catm.feature.documents.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.documents.entities.DocumentsEnvelopeX
import ru.metasharks.catm.feature.documents.services.DocumentsApi
import javax.inject.Inject

fun interface SearchDocumentsUseCase {

    operator fun invoke(
        typeId: Int,
        searchQuery: String,
        nextUrl: String?
    ): Single<DocumentsEnvelopeX>
}

internal class SearchDocumentsUseCaseImpl @Inject constructor(
    private val documentsApi: DocumentsApi,
) : SearchDocumentsUseCase {

    override fun invoke(
        typeId: Int,
        searchQuery: String,
        nextUrl: String?
    ): Single<DocumentsEnvelopeX> {
        return if (nextUrl != null) {
            documentsApi.getDocuments(nextUrl)
        } else {
            documentsApi.searchDocuments(typeId, searchQuery)
        }
    }
}
