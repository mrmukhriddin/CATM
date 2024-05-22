package ru.metasharks.catm.core.license

import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import javax.inject.Inject

interface LicenseExpiredProvider {

    operator fun invoke(): Boolean

    fun set(isLicenseExpired: Boolean)
}

internal class LicenseExpiredProviderImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider,
) : LicenseExpiredProvider {

    private var isLicenseExpired: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        PREF_LICENSE_EXPIRED,
        false
    )

    override fun invoke(): Boolean {
        return isLicenseExpired
    }

    override fun set(isLicenseExpired: Boolean) {
        this.isLicenseExpired = isLicenseExpired
    }

    companion object {

        const val PREF_LICENSE_EXPIRED = "pref.license_expired"
    }
}
