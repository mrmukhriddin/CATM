package ru.metasharks.catm.feature.aboutapp.ui.di

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.AboutAppScreen
import ru.metasharks.catm.feature.aboutapp.ui.AboutAppActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AboutAppUiModule private constructor() {

    companion object {

        @Provides
        fun provideAboutAppScreen(): AboutAppScreen {
            return AboutAppScreen {
                ActivityScreen {
                    Intent(it, AboutAppActivity::class.java)
                }
            }
        }
    }
}
