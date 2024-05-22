package ru.metasharks.catm.core.network.offline

import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineModeProvider @Inject constructor(
    private val preferencesProvider: PreferencesProvider
) {

    var isInOfflineMode by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_OFFLINE_MODE,
        false
    )

    val isInOnlineMode: Boolean
        get() = isInOfflineMode.not()

    companion object {

        private const val PREF_OFFLINE_MODE = "pref.offline_mode"
    }
}
