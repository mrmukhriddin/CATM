package ru.metasharks.catm.model.api

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.R
import ru.metasharks.catm.core.network.ApiUrlProvider
import ru.metasharks.catm.core.network.switchurl.BaseUrlSwitcher
import javax.inject.Inject

internal class ApiUrlProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val baseUrlSwitcher: BaseUrlSwitcher,
) : ApiUrlProvider {

    override val baseUrl: String
        get() = if (baseUrlSwitcher.isDemo()) {
            context.getString(R.string.demo_url)
        } else {
            context.getString(R.string.base_url)
        }
}
