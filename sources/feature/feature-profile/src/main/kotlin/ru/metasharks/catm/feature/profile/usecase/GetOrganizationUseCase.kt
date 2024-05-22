package ru.metasharks.catm.feature.profile.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.entities.OrganizationX
import ru.metasharks.catm.feature.profile.services.ProfileApi
import javax.inject.Inject

fun interface GetOrganizationUseCase {

    operator fun invoke(): Single<OrganizationX>
}

internal class GetOrganizationUseCaseImpl @Inject constructor(
    private val profileApi: ProfileApi,
) : GetOrganizationUseCase {

    override fun invoke(): Single<OrganizationX> {
        return profileApi.getOrganization()
    }
}
