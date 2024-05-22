package ru.metasharks.catm.core.network.request.csrf

import ru.metasharks.catm.core.network.cookie.CookieKeeper

interface CsrfTokenStorage : CookieKeeper {

    var csrfToken: String?
}
