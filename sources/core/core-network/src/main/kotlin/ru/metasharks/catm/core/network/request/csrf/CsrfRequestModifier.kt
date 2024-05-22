package ru.metasharks.catm.core.network.request.csrf

import okhttp3.HttpUrl
import okhttp3.Request
import ru.metasharks.catm.core.network.PredefinedHeaders
import ru.metasharks.catm.core.network.request.RequestModifier
import ru.metasharks.catm.core.network.request.RequestModifiers.isModifiable

class CsrfRequestModifier(private val csrfTokenStorage: CsrfTokenStorage) : RequestModifier {

    override fun appliesTo(request: Request): Boolean =
        request.isModifiable(PredefinedHeaders.CSRF)

    override fun modify(request: Request, builder: Request.Builder) {
        builder.removeHeader(PredefinedHeaders.CSRF)
        val csrfToken = csrfTokenStorage.csrfToken
        if (!csrfToken.isNullOrEmpty()) {
            builder.addHeader(PredefinedHeaders.CSRF, csrfToken)
        }
    }

    override fun modify(request: Request, builder: HttpUrl.Builder) = Unit
}
