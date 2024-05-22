package ru.metasharks.catm.core.storage.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.core.storage.prefs.PreferencesProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StorageModule private constructor() {

    @Binds
    abstract fun bindPreferencesProvider(impl: PreferencesProviderImpl): PreferencesProvider

    companion object {

        @Provides
        @Singleton
        fun provideFileManager(context: Context): FileManager {
            return FileManager(context)
        }
    }
}
