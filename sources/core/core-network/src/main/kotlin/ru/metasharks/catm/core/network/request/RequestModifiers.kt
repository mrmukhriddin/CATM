package ru.metasharks.catm.core.network.request

import okhttp3.Request
import ru.metasharks.catm.core.network.PredefinedHeaders

object RequestModifiers {

    const val CSRF_TOKEN: String = PredefinedHeaders.CSRF + ": true"
    const val AUTH: String = PredefinedHeaders.AUTH + ": true"

    internal fun Request.isModifiable(requestHeader: String): Boolean {
        val headerValue = header(requestHeader)
        return headerValue != null && headerValue == "true"
    }
}
