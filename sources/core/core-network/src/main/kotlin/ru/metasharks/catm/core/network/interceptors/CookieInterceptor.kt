package ru.metasharks.catm.core.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class CookieInterceptor @Inject internal constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        val cookiesArr = originalResponse.headers("Set-Cookie")
        if (cookiesArr.isNotEmpty()) {
            Timber.d("Cookies:\n")
            for (cookie in cookiesArr) {
                var i = 0
                var j = cookie.indexOf('=', i)
                val key = cookie.substring(i, j)
                i = j + 1
                j = cookie.indexOf(';', i)
                val value = cookie.substring(i, j)
                Timber.d("$key - $value")
            }
        }
        return originalResponse
    }
}
