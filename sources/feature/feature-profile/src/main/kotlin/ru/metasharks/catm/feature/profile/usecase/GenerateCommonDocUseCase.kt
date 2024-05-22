package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface GenerateCommonDocUseCase {

    operator fun invoke(userId: Int): Completable
}

internal class GenerateCommonDocUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
) : GenerateCommonDocUseCase {

    override fun invoke(userId: Int): Completable {
        return profileApi.generateCommonDoc(userId)
    }
}
