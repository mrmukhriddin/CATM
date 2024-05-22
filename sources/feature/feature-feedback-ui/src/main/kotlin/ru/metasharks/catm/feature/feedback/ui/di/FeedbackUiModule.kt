package ru.metasharks.catm.feature.feedback.ui.di

import com.github.terrakok.cicerone.androidx.FragmentScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.fragments.FeedbackScreen
import ru.metasharks.catm.feature.feedback.ui.FeedbackFragment

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeedbackUiModule private constructor() {

    companion object {

        @Provides
        fun provideFeedbackFragment(): FeedbackScreen = FeedbackScreen {
            FragmentScreen {
                FeedbackFragment()
            }
        }
    }
}
