package ru.metasharks.catm.feature.profile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.network.ApiClient
import ru.metasharks.catm.feature.profile.role.RoleManager
import ru.metasharks.catm.feature.profile.role.RoleManagerImpl
import ru.metasharks.catm.feature.profile.role.RoleProvider
import ru.metasharks.catm.feature.profile.services.LicenseApi
import ru.metasharks.catm.feature.profile.services.ProfileApi
import ru.metasharks.catm.feature.profile.usecase.GenerateCommonDocUseCase
import ru.metasharks.catm.feature.profile.usecase.GenerateCommonDocUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetOrganizationUseCase
import ru.metasharks.catm.feature.profile.usecase.GetOrganizationUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetQrCodeUseCase
import ru.metasharks.catm.feature.profile.usecase.GetQrCodeUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetUserByIdUseCase
import ru.metasharks.catm.feature.profile.usecase.GetUserByIdUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetWorkersOfflineUseCase
import ru.metasharks.catm.feature.profile.usecase.GetWorkersOfflineUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.GetWorkersUseCase
import ru.metasharks.catm.feature.profile.usecase.GetWorkersUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.SearchWorkersUseCase
import ru.metasharks.catm.feature.profile.usecase.SearchWorkersUseCaseImpl
import ru.metasharks.catm.feature.profile.usecase.license.UpdateLicenseUseCase
import ru.metasharks.catm.feature.profile.usecase.license.UpdateLicenseUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileModule private constructor() {

    @Binds
    abstract fun bindRoleManager(impl: RoleManagerImpl): RoleManager

    @Binds
    abstract fun bindRoleProvider(impl: RoleManagerImpl): RoleProvider

    @Binds
    abstract fun bindGetCurrentUserUseCase(impl: GetCurrentUserUseCaseImpl): GetCurrentUserUseCase

    @Binds
    abstract fun bindGetUserByIdUseCase(impl: GetUserByIdUseCaseImpl): GetUserByIdUseCase

    @Binds
    abstract fun bindGetWorkersUseCase(impl: GetWorkersUseCaseImpl): GetWorkersUseCase

    @Binds
    abstract fun bindSearchWorkersUseCase(impl: SearchWorkersUseCaseImpl): SearchWorkersUseCase

    @Binds
    abstract fun bindGetQrCodeUseCase(impl: GetQrCodeUseCaseImpl): GetQrCodeUseCase

    @Binds
    abstract fun bindGetOrganizationUseCase(impl: GetOrganizationUseCaseImpl): GetOrganizationUseCase

    @Binds
    abstract fun bindGetWorkersOfflineUseCase(impl: GetWorkersOfflineUseCaseImpl): GetWorkersOfflineUseCase

    @Binds
    abstract fun bindGenerateCommonDocUseCase(impl: GenerateCommonDocUseCaseImpl): GenerateCommonDocUseCase

    @Binds
    abstract fun bindUpdateLicenseUseCase(impl: UpdateLicenseUseCaseImpl): UpdateLicenseUseCase

    companion object {

        @Provides
        fun provideProfileApi(client: ApiClient): ProfileApi =
            client.createService(ProfileApi::class.java)

        @Provides
        fun provideLicenseApi(client: ApiClient): LicenseApi =
            client.createService(LicenseApi::class.java)
    }
}
