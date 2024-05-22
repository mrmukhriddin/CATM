package ru.metasharks.catm.model.api

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.R
import ru.metasharks.catm.core.network.MediaUrlProvider
import javax.inject.Inject

internal class MediaUrlProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaUrlProvider {

    override val mediaUrl: String
        get() = context.getString(R.string.media_url)
}
