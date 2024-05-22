package ru.metasharks.catm.feature.documents.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.network.ApiClient
import ru.metasharks.catm.feature.documents.services.DocumentsApi
import ru.metasharks.catm.feature.documents.usecase.GetDocumentTypesUseCase
import ru.metasharks.catm.feature.documents.usecase.GetDocumentTypesUseCaseImpl
import ru.metasharks.catm.feature.documents.usecase.GetDocumentsUseCase
import ru.metasharks.catm.feature.documents.usecase.GetDocumentsUseCaseImpl
import ru.metasharks.catm.feature.documents.usecase.SearchDocumentsUseCase
import ru.metasharks.catm.feature.documents.usecase.SearchDocumentsUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DocumentsModule private constructor() {

    @Binds
    abstract fun bindGetDocumentTypesUseCase(impl: GetDocumentTypesUseCaseImpl): GetDocumentTypesUseCase

    @Binds
    abstract fun bindSearchDocumentsUseCase(impl: SearchDocumentsUseCaseImpl): SearchDocumentsUseCase

    @Binds
    abstract fun bindGetDocumentsUseCase(impl: GetDocumentsUseCaseImpl): GetDocumentsUseCase

    companion object {

        @Provides
        fun provideDocumentsApi(client: ApiClient): DocumentsApi {
            return client.createService(DocumentsApi::class.java)
        }
    }
}
