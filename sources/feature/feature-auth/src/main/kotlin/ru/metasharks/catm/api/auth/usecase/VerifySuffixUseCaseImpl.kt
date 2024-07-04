package ru.metasharks.catm.api.auth.usecase;

import io.reactivex.rxjava3.core.Completable
import ru.metasharks.catm.api.auth.credentials.CredentialsProvider
import ru.metasharks.catm.api.auth.entities.request.RequestVerifySuffixData
import ru.metasharks.catm.api.auth.services.AuthApi
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import javax.inject.Inject


fun interface VerifySuffixUseCase {

    operator fun invoke(key: String): Completable
}

internal class VerifySuffixUseCaseImpl @Inject constructor(
    private val authApi: AuthApi,
) : VerifySuffixUseCase {
    override fun invoke(key: String): Completable {
        return authApi.verifySuffix(RequestVerifySuffixData(key))
            .flatMapCompletable { response ->
                if (response.match) {
                    Completable.complete()
                } else {
                    Completable.error(Exception("Suffix does not match"))
                }
            }    }


}
