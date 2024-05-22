package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.entities.quiz.request.PassQuizRequestX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface PassQuizUseCase {

    operator fun invoke(request: PassQuizRequestX): Single<Boolean>
}

internal class PassQuizUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
) : PassQuizUseCase {

    override fun invoke(request: PassQuizRequestX): Single<Boolean> =
        api.passQuiz(request).toSingle { true }.onErrorReturn { false }
}
