package ru.metasharks.catm.api.auth.services

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.metasharks.catm.api.auth.entities.LoginResponse
import ru.metasharks.catm.api.auth.entities.SetCsrfResponse
import ru.metasharks.catm.core.network.PredefinedHeaders
import ru.metasharks.catm.core.network.request.RequestModifiers

interface AuthApi {

    @GET("auth/set-csrf/")
    fun setCsrfToken(): Single<SetCsrfResponse>

    @Headers(RequestModifiers.CSRF_TOKEN)
    @POST("auth/login/")
    fun login(
        @Header(PredefinedHeaders.AUTH) authCredentials: String
    ): Single<LoginResponse>

    @Headers(RequestModifiers.AUTH)
    @POST("auth/logout")
    fun logout(): Completable
}
