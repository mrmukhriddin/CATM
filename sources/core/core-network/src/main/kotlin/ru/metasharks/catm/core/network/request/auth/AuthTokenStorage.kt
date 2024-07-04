package ru.metasharks.catm.core.network.request.auth

import org.joda.time.LocalDateTime
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject
interface AuthTokenStorage {

    fun setNewAuthToken(authToken: String, expirationDate: String)

    fun setTemporaryAuthToken(authToken: String, expirationDate: String)

    fun clear()

    var firstLogin: Boolean

    var expirationDate: String?

    var authToken: String?

    val isValid: Boolean

    var temporaryExpirationDate: String?

    var temporaryAuthToken: String?

    val isTemporaryValid: Boolean

    fun getTemporaryAuthTokenInfo(): Pair<String?, String?>
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

    override var temporaryExpirationDate: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_TEMPORARY_EXPIRATION_DATE,
        null
    )

    override var temporaryAuthToken: String? by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_TEMPORARY_AUTH_TOKEN,
        null
    )

    override val isTemporaryValid: Boolean
        get() = temporaryExpirationDate?.let { expirationDateString ->
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

    override fun setTemporaryAuthToken(authToken: String, expirationDate: String) {
        this.temporaryAuthToken = authToken
        this.temporaryExpirationDate = expirationDate
    }

    override fun clear() {
        this.authToken = null
        this.expirationDate = null
        this.temporaryAuthToken = null
        this.temporaryExpirationDate = null
    }

    override fun getTemporaryAuthTokenInfo(): Pair<String?, String?> {
        return Pair(temporaryAuthToken, temporaryExpirationDate)
    }

    companion object {
        private const val PREF_AUTH_TOKEN = "pref.auth_token"
        private const val PREF_EXPIRATION_DATE = "pref.expiration_date"
        private const val PREF_FIRST_LOGIN = "pref.first_login"
        private const val PREF_TEMPORARY_AUTH_TOKEN = "pref.temporary_auth_token"
        private const val PREF_TEMPORARY_EXPIRATION_DATE = "pref.temporary_expiration_date"
    }
}


class AuthTokenStorageMock @Inject constructor() : AuthTokenStorage {

    override fun setNewAuthToken(authToken: String, expirationDate: String) {
        // Mock implementation
    }

    override fun setTemporaryAuthToken(authToken: String, expirationDate: String) {
        // Mock implementation
    }

    override fun clear() {
        // Mock implementation
    }

    override var firstLogin: Boolean
        get() = true
        set(value) {}

    override var expirationDate: String?
        get() = "2100-01-01T00:00:00.000000"
        set(value) {}

    override var authToken: String?
        get() = "135ff73cef1875a4a170243f524cd466f4e6185f70be8249eb48273a33fbe888"
        set(value) {}

    override val isValid: Boolean
        get() = true

    override var temporaryExpirationDate: String?
        get() = "2100-01-01T00:00:00.000000"
        set(value) {}

    override var temporaryAuthToken: String?
        get() = "temporaryToken"
        set(value) {}

    override val isTemporaryValid: Boolean
        get() = true

    override fun getTemporaryAuthTokenInfo(): Pair<String?, String?> {
        return Pair(temporaryAuthToken, temporaryExpirationDate)
    }
}
