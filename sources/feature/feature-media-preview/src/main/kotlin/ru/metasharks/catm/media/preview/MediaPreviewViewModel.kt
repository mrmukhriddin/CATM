package ru.metasharks.catm.media.preview

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.navigation.ApplicationRouter
import javax.inject.Inject

@HiltViewModel
internal class MediaPreviewViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
) : ViewModel() {

    fun back() {
        appRouter.exit()
    }
}
