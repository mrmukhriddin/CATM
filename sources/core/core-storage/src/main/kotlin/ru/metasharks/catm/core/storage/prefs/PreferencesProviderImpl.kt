package ru.metasharks.catm.core.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.core.storage.R
import javax.inject.Inject

internal class PreferencesProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesProvider {

    override fun getKey(stringRes: Int): String = context.getString(stringRes)

    override val applicationPreferences: SharedPreferences
        get() = context.getSharedPreferences(
            context.getString(R.string.prefs_file_name),
            Context.MODE_PRIVATE
        )
}
