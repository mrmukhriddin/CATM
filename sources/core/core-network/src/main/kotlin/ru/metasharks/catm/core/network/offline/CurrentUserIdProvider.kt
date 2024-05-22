package ru.metasharks.catm.core.network.offline

import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import javax.inject.Inject

class CurrentUserIdProvider @Inject constructor(
    private val preferencesProvider: PreferencesProvider
) {

    operator fun invoke(): Long {
        return currentUserId
    }

    var currentUserId by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_CURRENT_USER_ID,
        -1L
    )

    companion object {

        private const val PREF_CURRENT_USER_ID = "pref.current_user_id"
    }
}
