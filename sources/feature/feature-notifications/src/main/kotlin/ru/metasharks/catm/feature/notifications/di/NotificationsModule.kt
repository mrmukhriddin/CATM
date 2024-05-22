package ru.metasharks.catm.feature.notifications.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.feature.notifications.repository.NotificationsRepository
import ru.metasharks.catm.feature.notifications.repository.NotificationsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsModule private constructor() {

    @Binds
    abstract fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository
}
