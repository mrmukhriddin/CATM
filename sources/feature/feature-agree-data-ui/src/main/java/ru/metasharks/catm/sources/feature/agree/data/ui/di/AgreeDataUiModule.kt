package ru.metasharks.catm.sources.feature.agree.data.ui.di

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.AboutAppScreen
import ru.metasharks.catm.core.navigation.screens.AgreeDataScreen
import ru.metasharks.catm.sources.feature.agree.data.ui.AgreeDataActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AgreeDataUiModule {
    companion object {
        @Provides
        fun provideAgreeDataScreen(): AgreeDataScreen {
            return AgreeDataScreen {
                ActivityScreen {
                    Intent(it, AgreeDataActivity::class.java)
                }
            }
        }
    }
}