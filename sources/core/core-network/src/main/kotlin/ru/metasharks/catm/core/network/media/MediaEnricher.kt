package ru.metasharks.catm.core.network.media

import ru.metasharks.catm.core.network.MediaUrlProvider
import javax.inject.Inject

class MediaEnricher @Inject constructor(private val mediaUrlProvider: MediaUrlProvider) {

    fun enrichString(mediaPath: String?): String? {
        if (mediaPath == null) return null
        return mediaUrlProvider.mediaUrl + mediaPath
    }
}
