package ru.metasharks.catm.core.network.request.auth

import org.joda.time.LocalDateTime
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject

interface AuthTokenStorage {

    fun setNewAuthToken(authToken: String, expirationDate: String)

    fun clear()

    var firstLogin: Boolean

    var expirationDate: String?

    var authToken: String?

    val isValid: Boolean
}


class AuthTokenStorageImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider
) : AuthTokenStorage {

    override var expirationDate: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_EXPIRATION_DATE,
        null
    )

    override var authToken: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_AUTH_TOKEN,
        null
    )

    override val isValid: Boolean
        get() = expirationDate?.let { expirationDateString ->
            val expirationDate = LocalDateUtils.parseISO8601toLocalDateTime(expirationDateString)
            return expirationDate.isAfter(LocalDateTime.now())
        } ?: false


    override var firstLogin: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_FIRST_LOGIN,
        true
    )

    override fun setNewAuthToken(authToken: String, expirationDate: String) {
        this.authToken = authToken
        this.expirationDate = expirationDate
    }

    override fun clear() {
        this.authToken = null
        this.expirationDate = null
    }

    companion object {
        private const val PREF_AUTH_TOKEN = "pref.auth_token"
        private const val PREF_EXPIRATION_DATE = "pref.expiration_date"
        private const val PREF_FIRST_LOGIN = "pref.first_login"
    }
}




class AuthTokenStorageMock @Inject constructor() : AuthTokenStorage {

    override fun setNewAuthToken(authToken: String, expirationDate: String) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }



    override var firstLogin: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override var expirationDate: String?
        get() = "2100-01-01T00:00:00.000000"
        set(value) {}

    override var authToken: String?
        get() = "135ff73cef1875a4a170243f524cd466f4e6185f70be8249eb48273a33fbe888"
        set(value) {}

    override val isValid: Boolean
        get() = TODO("Not yet implemented")
}
