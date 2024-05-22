package ru.metasharks.catm.feature.documents.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.documents.entities.DocumentTypeX
import ru.metasharks.catm.feature.documents.services.DocumentsApi
import javax.inject.Inject

fun interface GetDocumentTypesUseCase {

    operator fun invoke(): Single<List<DocumentTypeX>>
}

internal class GetDocumentTypesUseCaseImpl @Inject constructor(
    private val documentsApi: DocumentsApi,
) : GetDocumentTypesUseCase {

    override fun invoke(): Single<List<DocumentTypeX>> {
        return documentsApi.getDocumentsTypes()
    }
}
