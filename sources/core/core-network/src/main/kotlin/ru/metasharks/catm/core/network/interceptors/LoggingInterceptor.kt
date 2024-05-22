package ru.metasharks.catm.core.network.interceptors

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoggingInterceptor @Inject internal constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Timber.tag(LOG_TAG).d("--> %s %s", request.method, request.url)
        logHeaders(request.headers)
        logBody(request.body)
        val startNs = System.nanoTime()
        val response = chain.proceed(request)
        val elapsedNs = System.nanoTime() - startNs
        Timber.tag(LOG_TAG).d(
            "<-- %s %s (%d ms)",
            request.method,
            request.url,
            TimeUnit.NANOSECONDS.toMillis(elapsedNs)
        )
        logBody(requireNotNull(response.body))
        return response
    }

    private fun logHeaders(headers: Headers?) {
        if (headers == null || headers.size == 0) {
            return
        }
        val sb = StringBuilder(String.format("Headers (%d):", headers.size))
        for (name in headers.names()) {
            val value = headers[name]
            sb.append("\n- ")
                .append(name)
                .append(": ")
                .append(value)
        }
        Timber.tag(LOG_TAG).d(sb.toString())
    }

    @Throws(IOException::class)
    private fun logBody(body: RequestBody?) {
        if (body == null) {
            return
        }
        val contentLength = body.contentLength()
        val contentType = body.contentType()
        if (contentLength in 1 until MAX_PRINTABLE_BODY_SIZE && isPrintable(contentType)) {
            val buffer = Buffer()
            body.writeTo(buffer)

            val charset = contentType!!.charset(UTF_8)
            Timber.tag(LOG_TAG).d(buffer.readString(charset!!))
        }
    }

    @Throws(IOException::class)
    private fun logBody(body: ResponseBody) {
        val contentLength = body.contentLength()
        val contentType = body.contentType()
        if (contentLength in 1 until MAX_PRINTABLE_BODY_SIZE && isPrintable(contentType)) {
            val source = body.source()
            source.request(java.lang.Long.MAX_VALUE)
            val buffer = source.buffer

            val charset = contentType!!.charset(UTF_8)
            Timber.tag(LOG_TAG).d(buffer.clone().readString(charset!!))
        }
    }

    private fun isPrintable(contentType: MediaType?): Boolean {
        if (contentType == null) {
            return false
        }
        val typeSubtype = contentType.type + "/" + contentType.subtype
        return PRINTABLE_CONTENT_TYPES.contains(typeSubtype)
    }

    companion object {
        private val UTF_8 = Charset.forName("UTF-8")
        private const val LOG_TAG = "Network"
        private const val MAX_PRINTABLE_BODY_SIZE: Long = 5024
        private val PRINTABLE_CONTENT_TYPES = listOf(
            "text/plain",
            "text/html",
            "text/xml",
            "application/json",
            "application/x-www-form-urlencoded"
        )
    }
}
