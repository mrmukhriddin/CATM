package ru.metasharks.catm.feature.profile.services

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.profile.entities.OrganizationX
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import ru.metasharks.catm.feature.profile.entities.user.WorkerEnvelopeX

interface ProfileApi {

    @GET("users/current-user")
    @Headers(RequestModifiers.AUTH)
    fun getUser(): Single<UserProfileX>

    @GET("users/qr")
    @Headers(RequestModifiers.AUTH)
    fun getQr(): Single<ResponseBody>

    @GET("users/{user_id}")
    @Headers(RequestModifiers.AUTH)
    fun getUser(@Path("user_id") userId: Int): Single<UserProfileX>

    @GET("users")
    @Headers(RequestModifiers.AUTH)
    fun getWorkers(): Single<WorkerEnvelopeX>

    @GET("users")
    @Headers(RequestModifiers.AUTH)
    fun searchWorkers(
        @Query("search") searchRequest: String,
    ): Single<WorkerEnvelopeX>

    @GET
    @Headers(RequestModifiers.AUTH)
    fun getWorkers(
        @Url url: String
    ): Single<WorkerEnvelopeX>

    @GET("users/organization")
    @Headers(RequestModifiers.AUTH)
    fun getOrganization(): Single<OrganizationX>

    @POST("users/{user_id}/common-document")
    @Headers(RequestModifiers.AUTH)
    fun generateCommonDoc(
        @Path("user_id") userId: Int,
    ): Completable
}
