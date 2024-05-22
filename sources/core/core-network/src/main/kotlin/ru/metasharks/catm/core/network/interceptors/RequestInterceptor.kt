package ru.metasharks.catm.core.network.interceptors

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.metasharks.catm.core.network.request.RequestModifier

class RequestInterceptor(private val requestModifiers: List<RequestModifier>) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val resultingRequest = applyModifiers(request)
        return chain.proceed(resultingRequest)
    }

    private fun applyModifiers(request: Request): Request {
        val builder: Request.Builder = request.newBuilder()
        val urlBuilder: HttpUrl.Builder = request.url.newBuilder()

        for (modifier in requestModifiers) {
            if (modifier.appliesTo(request)) {
                modifier.modify(request, builder)
                modifier.modify(request, urlBuilder)
            }
        }

        return builder
            .url(urlBuilder.build())
            .build()
    }
}
