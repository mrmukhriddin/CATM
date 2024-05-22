package ru.metasharks.catm.media.preview.di

import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.MediaPreviewScreen
import ru.metasharks.catm.media.preview.MediaPreviewActivity

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaPreviewModule private constructor() {

    companion object {

        @Provides
        fun provideMediaPreviewScreen(): MediaPreviewScreen = MediaPreviewScreen { fileUri, title ->
            ActivityScreen { context ->
                MediaPreviewActivity.createIntent(context, fileUri, title)
            }
        }
    }
}
