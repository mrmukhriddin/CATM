package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface SignBriefingUseCase {

    operator fun invoke(briefingId: Int): Completable
}

internal class SignBriefingUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
) : SignBriefingUseCase {

    override operator fun invoke(briefingId: Int): Completable = api.signBriefing(briefingId)
}
