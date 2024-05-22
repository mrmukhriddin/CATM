package ru.metasharks.catm.feature.documents.services

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.documents.entities.DocumentTypeX
import ru.metasharks.catm.feature.documents.entities.DocumentsEnvelopeX

interface DocumentsApi {

    @GET("documents/types")
    @Headers(RequestModifiers.AUTH)
    fun getDocumentsTypes(): Single<List<DocumentTypeX>>

    @GET("documents")
    @Headers(RequestModifiers.AUTH)
    fun getDocuments(
        @Query("type") typeId: Int,
    ): Single<DocumentsEnvelopeX>

    @GET("documents")
    @Headers(RequestModifiers.AUTH)
    fun searchDocuments(
        @Query("type") typeId: Int,
        @Query("title") searchRequest: String,
    ): Single<DocumentsEnvelopeX>

    @GET
    @Headers(RequestModifiers.AUTH)
    fun getDocuments(
        @Url nextUrl: String,
    ): Single<DocumentsEnvelopeX>
}
