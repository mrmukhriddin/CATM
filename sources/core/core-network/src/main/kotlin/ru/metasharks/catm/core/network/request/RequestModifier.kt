package ru.metasharks.catm.core.network.request

import okhttp3.HttpUrl
import okhttp3.Request

interface RequestModifier {

    fun appliesTo(request: Request): Boolean

    fun modify(request: Request, builder: Request.Builder)

    fun modify(request: Request, builder: HttpUrl.Builder)
}
