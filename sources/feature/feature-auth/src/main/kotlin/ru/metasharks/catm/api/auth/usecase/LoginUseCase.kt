package ru.metasharks.catm.api.auth.usecase

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.api.auth.credentials.CredentialsProvider
import ru.metasharks.catm.api.auth.services.AuthApi
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import javax.inject.Inject

fun interface LoginUseCase {

    operator fun invoke(username: String, password: String): Completable

}

internal class LoginUseCaseImpl @Inject constructor(
    private val authApi: AuthApi,
    private val credentialsProvider: CredentialsProvider,
    private val authTokenStorage: AuthTokenStorage,
) : LoginUseCase {

    override fun invoke(username: String, password: String): Completable {
        return authApi.setCsrfToken()
            .flatMap {
                authApi.login(credentialsProvider.getCredentials(username, password))
            }
            .doOnSuccess { loginResponse ->


                if (!loginResponse.first_login) {
                    authTokenStorage.setNewAuthToken(loginResponse.token, loginResponse.expiry)
                } else {
                    authTokenStorage.setTemporaryAuthToken(
                        loginResponse.token,
                        loginResponse.expiry
                    )
                }
                authTokenStorage.firstLogin = loginResponse.first_login
            }.ignoreElement()
    }

    companion object {

        private const val FIELD_DETAIL = "detail"
    }
}
