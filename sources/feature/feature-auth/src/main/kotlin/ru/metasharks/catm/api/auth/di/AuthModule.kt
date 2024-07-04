package ru.metasharks.catm.api.auth.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.api.auth.credentials.CredentialsProvider
import ru.metasharks.catm.api.auth.credentials.CredentialsProviderImpl
import ru.metasharks.catm.api.auth.services.AuthApi
import ru.metasharks.catm.api.auth.usecase.LoginUseCase
import ru.metasharks.catm.api.auth.usecase.LoginUseCaseImpl
import ru.metasharks.catm.api.auth.usecase.LogoutUseCase
import ru.metasharks.catm.api.auth.usecase.LogoutUseCaseImpl
import ru.metasharks.catm.api.auth.usecase.VerifySuffixUseCase
import ru.metasharks.catm.api.auth.usecase.VerifySuffixUseCaseImpl
import ru.metasharks.catm.core.network.ApiClient

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule private constructor() {

    @Binds
    abstract fun bindCredentialsProvider(impl: CredentialsProviderImpl): CredentialsProvider

    @Binds
    abstract fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase

    @Binds
    abstract fun bindLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    abstract fun bindVerifySuffixUseCase(impl: VerifySuffixUseCaseImpl): VerifySuffixUseCase

    companion object {

        @Provides
        fun provideAuthApi(client: ApiClient): AuthApi = client.createService(AuthApi::class.java)
    }
}
