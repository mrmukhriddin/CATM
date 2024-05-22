package ru.metasharks.catm.utils

import ru.metasharks.catm.core.utils.BuildConfig

enum class FeatureToggle {
    SHOW_ROLES, DEMO_URL, DELETE_PENDING, SIGN_DAILY_PERMIT, SIGN_NEW_ADDED_WORKERS;

    val isEnabled: Boolean
        get() = (if (BuildConfig.DEBUG) ENABLED else RELEASE_ENABLED).contains(this)

    companion object {
        private val ENABLED: Set<FeatureToggle> = setOf(
            SHOW_ROLES, DEMO_URL,
        )

        private val RELEASE_ENABLED: Set<FeatureToggle> = setOf()
    }
}
