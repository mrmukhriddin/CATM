package ru.metasharks.catm.feature.aboutapp.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.metasharks.catm.core.navigation.ApplicationRouter
import javax.inject.Inject

@HiltViewModel
internal class AboutAppViewModel @Inject constructor(
    private val appRouter: ApplicationRouter
) : ViewModel() {

    fun exit() {
        appRouter.exit()
    }
}
