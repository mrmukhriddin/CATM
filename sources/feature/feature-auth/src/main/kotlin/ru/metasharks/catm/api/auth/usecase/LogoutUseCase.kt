package ru.metasharks.catm.api.auth.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.api.auth.services.AuthApi
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import javax.inject.Inject

fun interface LogoutUseCase {

    operator fun invoke(): Completable
}

internal class LogoutUseCaseImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authTokenStorage: AuthTokenStorage,
) : LogoutUseCase {

    override fun invoke(): Completable {
        return authApi.logout().andThen(Completable.fromAction(authTokenStorage::clear))
    }
}
