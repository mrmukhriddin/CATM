package ru.metasharks.catm.feature.feedback.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.feedback.entities.SendFeedbackRequestX
import ru.metasharks.catm.feature.feedback.service.FeedbackApi
import javax.inject.Inject

fun interface SendFeedbackUseCase {

    operator fun invoke(request: SendFeedbackRequestX): Completable
}

internal class SendFeedbackUseCaseImpl @Inject constructor(
    private val feedbackApi: FeedbackApi,
) : SendFeedbackUseCase {

    override fun invoke(request: SendFeedbackRequestX): Completable {
        return feedbackApi.sendFeedback(request)
    }
}
