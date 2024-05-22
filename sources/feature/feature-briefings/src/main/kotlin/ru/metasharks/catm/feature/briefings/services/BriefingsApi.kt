package ru.metasharks.catm.feature.briefings.services

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import ru.metasharks.catm.core.network.request.RequestModifiers
import ru.metasharks.catm.feature.briefings.entities.BriefingCategoryX
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuizX
import ru.metasharks.catm.feature.briefings.entities.quiz.request.PassQuizRequestX

interface BriefingsApi {

    @GET("briefings/categories")
    @Headers(RequestModifiers.AUTH)
    fun getCategories(): Single<List<BriefingCategoryX>>

    @GET("briefings/types")
    @Headers(RequestModifiers.AUTH)
    fun getTypes(): Single<List<BriefingTypeX>>

    @GET("briefings")
    @Headers(RequestModifiers.AUTH)
    fun getBriefings(): Single<List<BriefingX>>

    @GET("briefings/quiz/{id}")
    @Headers(RequestModifiers.AUTH)
    fun getQuiz(@Path("id") id: Long): Single<QuizX>

    @POST("briefings/quiz")
    @Headers(RequestModifiers.AUTH)
    fun passQuiz(@Body request: PassQuizRequestX): Completable

    @POST("briefings/{id}/sign")
    @Headers(RequestModifiers.AUTH)
    fun signBriefing(@Path("id") briefingId: Int): Completable
}
