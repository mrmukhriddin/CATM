package ru.metasharks.catm.core.license.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.license.LicenseExpiredProvider
import ru.metasharks.catm.core.license.LicenseExpiredProviderImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LicenseModule private constructor() {

    @Binds
    abstract fun bindLicenseExpiredProvider(impl: LicenseExpiredProviderImpl): LicenseExpiredProvider
}
