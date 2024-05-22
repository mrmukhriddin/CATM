package ru.metasharks.catm.feature.profile.di

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.CoreScreen
import ru.metasharks.catm.core.navigation.screens.CurrentProfileScreen
import ru.metasharks.catm.core.navigation.screens.ProfileScreen
import ru.metasharks.catm.core.navigation.screens.WorkersScreen
import ru.metasharks.catm.core.navigation.screens.fragments.MainScreen
import ru.metasharks.catm.core.navigation.screens.fragments.PlaceholderScreen
import ru.metasharks.catm.feature.profile.ui.CoreActivity
import ru.metasharks.catm.feature.profile.ui.main.MainFragment
import ru.metasharks.catm.feature.profile.ui.placeholder.PlaceholderFragment
import ru.metasharks.catm.feature.profile.ui.profile.ProfileActivity
import ru.metasharks.catm.feature.profile.ui.workers.WorkersActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileUiModule private constructor() {

    companion object {

        @Provides
        fun provideCoreScreen(): CoreScreen = CoreScreen {
            ActivityScreen { context ->
                Intent(context, CoreActivity::class.java)
            }
        }

        @Provides
        fun provideCurrentProfileScreen(): CurrentProfileScreen = CurrentProfileScreen {
            ActivityScreen { context ->
                Intent(context, ProfileActivity::class.java)
            }
        }

        @Provides
        fun provideProfileScreen(): ProfileScreen = ProfileScreen { id ->
            ActivityScreen { context ->
                ProfileActivity.createIntent(context, id)
            }
        }

        @Provides
        fun provideWorkersScreen(): WorkersScreen = WorkersScreen {
            ActivityScreen { context ->
                Intent(context, WorkersActivity::class.java)
            }
        }

        @Provides
        fun provideMainScreen(): MainScreen = MainScreen {
            FragmentScreen { MainFragment() }
        }

        @Provides
        fun providePlaceholderScreen(): PlaceholderScreen = PlaceholderScreen {
            FragmentScreen { PlaceholderFragment() }
        }
    }
}
