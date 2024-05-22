package ru.metasharks.catm.core.storage.prefs

import android.content.SharedPreferences
import androidx.annotation.StringRes

interface PreferencesProvider {

    fun getKey(@StringRes stringRes: Int): String

    val applicationPreferences: SharedPreferences
}
