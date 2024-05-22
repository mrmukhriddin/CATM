package ru.metasharks.catm.feature.education.ui.di

import com.github.terrakok.cicerone.androidx.FragmentScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.fragments.EducationScreen
import ru.metasharks.catm.feature.education.ui.EducationFragment

@Module
@InstallIn(SingletonComponent::class)
internal abstract class EducationUiModule private constructor() {

    companion object {

        @Provides
        fun provideEducationScreen() = EducationScreen {
            FragmentScreen { EducationFragment() }
        }
    }
}
