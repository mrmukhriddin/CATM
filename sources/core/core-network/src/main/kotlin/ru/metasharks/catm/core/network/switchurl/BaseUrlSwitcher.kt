package ru.metasharks.catm.core.network.switchurl

interface BaseUrlSwitcher {

    fun switchBaseUrl(): Boolean
    fun isDemo(): Boolean

    companion object {

        const val IS_DEMO = "pref.is_demo"
    }
}
