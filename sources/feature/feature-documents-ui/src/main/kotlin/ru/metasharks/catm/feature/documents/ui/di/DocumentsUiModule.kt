package ru.metasharks.catm.feature.documents.ui.di

import com.github.terrakok.cicerone.androidx.FragmentScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.fragments.DocumentsScreen
import ru.metasharks.catm.feature.documents.ui.DocumentsFragment

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DocumentsUiModule private constructor() {

    companion object {

        @Provides
        fun provideDocumentsScreen(): DocumentsScreen = DocumentsScreen {
            FragmentScreen { DocumentsFragment() }
        }
    }
}
