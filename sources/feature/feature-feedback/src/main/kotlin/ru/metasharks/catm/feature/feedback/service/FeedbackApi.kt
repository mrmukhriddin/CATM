package ru.metasharks.catm.feature.feedback.service

import io.reactivex.rxjava3.core.Completable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.feedback.entities.SendFeedbackRequestX

interface FeedbackApi {

    @POST("feedbacks/")
    @Headers(RequestModifiers.AUTH)
    fun sendFeedback(
        @Body body: SendFeedbackRequestX
    ): Completable
}
