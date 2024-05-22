package ru.metasharks.catm.core.network.csrf

import ru.metasharks.catm.core.network.request.csrf.CsrfTokenStorage
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import javax.inject.Inject

class CsrfTokenStorageImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider
) : CsrfTokenStorage {

    override var csrfToken: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_CSRF_TOKEN,
        null
    )

    override fun isForCookie(cookieName: String): Boolean = cookieName == "csrftoken"

    override fun saveCookie(cookieValue: String) {
        csrfToken = cookieValue
    }

    companion object {

        private const val PREF_CSRF_TOKEN = "pref.csrf_token"
    }
}
