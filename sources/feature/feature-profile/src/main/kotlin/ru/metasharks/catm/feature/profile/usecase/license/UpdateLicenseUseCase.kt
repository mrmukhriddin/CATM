package ru.metasharks.catm.feature.profile.usecase.license

import io.reactivex.rxjava3.core.Single
import org.joda.time.LocalDateTime
import ru.metasharks.catm.core.license.LicenseExpiredProvider
import ru.metasharks.catm.feature.profile.services.LicenseApi
import javax.inject.Inject

fun interface UpdateLicenseUseCase {

    operator fun invoke(): Single<Boolean>
}

internal class UpdateLicenseUseCaseImpl @Inject constructor(
    private val licenseApi: LicenseApi,
    private val licenseExpiredProvider: LicenseExpiredProvider,
) : UpdateLicenseUseCase {

    override fun invoke(): Single<Boolean> {
        return licenseApi.getLicenseDetails()
            .flatMap { details ->
                val isExpired = details.expireDate.isBefore(LocalDateTime.now())
                Single.fromCallable { licenseExpiredProvider.set(isExpired) }
                    .map { isExpired }
                    .onErrorReturn { false }
            }
    }
}
