package ru.metasharks.catm.core.network.switchurl

import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.utils.FeatureToggle
import javax.inject.Inject

class BaseUrlSwitcherImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider,
) : BaseUrlSwitcher {

    private var mIsDemo: Boolean by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        BaseUrlSwitcher.IS_DEMO,
        FeatureToggle.DEMO_URL.isEnabled
    )

    override fun isDemo(): Boolean {
        return mIsDemo
    }

    override fun switchBaseUrl(): Boolean {
        val currentValue = mIsDemo
        mIsDemo = currentValue.not()
        return currentValue.not()
    }
}
