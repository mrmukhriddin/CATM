package ru.metasharks.catm.core.network.cookie

import okhttp3.Cookie
import ru.metasharks.catm.core.network.request.csrf.CsrfTokenStorage
import javax.inject.Inject

class CookieStorage @Inject constructor(
    csrfTokenStorage: CsrfTokenStorage
) {

    private val cookieKeepers: List<CookieKeeper> = listOf(
        csrfTokenStorage
    )

    fun saveCookie(cookie: Cookie) {
        cookieKeepers.forEach { cookieKeeper ->
            if (cookieKeeper.isForCookie(cookie.name)) {
                cookieKeeper.saveCookie(cookie.value)
            }
        }
    }
}
