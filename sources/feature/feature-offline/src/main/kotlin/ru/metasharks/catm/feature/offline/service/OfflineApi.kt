package ru.metasharks.catm.feature.offline.service

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface OfflineApi {

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Single<ResponseBody>
}
