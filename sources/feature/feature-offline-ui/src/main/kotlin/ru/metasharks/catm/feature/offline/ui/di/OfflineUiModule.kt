package ru.metasharks.catm.feature.offline.ui.di

import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.OfflineSwitcherScreen
import ru.metasharks.catm.core.navigation.screens.SyncScreen
import ru.metasharks.catm.feature.offline.ui.OfflineSwitcherActivity
import ru.metasharks.catm.feature.offline.ui.sync.SyncActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OfflineUiModule private constructor() {

    companion object {

        @Provides
        fun provideSyncScreen() = SyncScreen {
            ActivityScreen { context ->
                SyncActivity.createIntent(context)
            }
        }

        @Provides
        fun provideOfflineSwitcherScreen() = OfflineSwitcherScreen { fromOnline, force ->
            ActivityScreen { context ->
                OfflineSwitcherActivity.createIntent(context, fromOnline, force)
            }
        }
    }
}
