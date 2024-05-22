package ru.metasharks.catm.core.network.request.auth

import okhttp3.HttpUrl
import okhttp3.Request
import ru.metasharks.catm.core.network.PredefinedHeaders
import ru.metasharks.catm.core.network.request.RequestModifier
import ru.metasharks.catm.core.network.request.RequestModifiers.isModifiable

class AuthRequestModifier(private val authTokenStorage: AuthTokenStorage) : RequestModifier {

    override fun appliesTo(request: Request): Boolean = request.isModifiable(PredefinedHeaders.AUTH)

    override fun modify(request: Request, builder: Request.Builder) {
        builder.removeHeader(PredefinedHeaders.AUTH)
        val authToken = authTokenStorage.authToken
        if (!authToken.isNullOrEmpty()) {
            builder.addHeader(PredefinedHeaders.AUTH, "Token $authToken")
        }
    }

    override fun modify(request: Request, builder: HttpUrl.Builder) = Unit
}
