package ru.metasharks.catm.feature.notifications.ui.di

import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.NotificationsScreen
import ru.metasharks.catm.feature.notifications.ui.NotificationsActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsUiModule private constructor() {

    companion object {

        @Provides
        fun providesNotificationsScreen(): NotificationsScreen = NotificationsScreen {
            ActivityScreen { context ->
                NotificationsActivity.createIntent(context)
            }
        }
    }
}
