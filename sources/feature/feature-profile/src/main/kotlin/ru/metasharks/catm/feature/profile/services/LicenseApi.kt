package ru.metasharks.catm.feature.profile.services

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.profile.entities.license.LicenseDetailsX

internal interface LicenseApi {

    @Headers(RequestModifiers.AUTH)
    @GET("license/detail/")
    fun getLicenseDetails(): Single<LicenseDetailsX>
}
