package ru.metasharks.catm.core.network.cookie

interface CookieKeeper {

    fun isForCookie(cookieName: String): Boolean

    fun saveCookie(cookieValue: String)
}
