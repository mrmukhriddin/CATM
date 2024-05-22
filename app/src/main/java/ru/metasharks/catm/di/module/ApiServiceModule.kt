package ru.metasharks.catm.di.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import ru.metasharks.catm.BuildConfig
import ru.metasharks.catm.core.network.ApiUrlProvider
import ru.metasharks.catm.core.network.MediaUrlProvider
import ru.metasharks.catm.core.network.cookie.CookieStorage
import ru.metasharks.catm.core.network.csrf.CsrfTokenStorageImpl
import ru.metasharks.catm.core.network.interceptors.CookieInterceptor
import ru.metasharks.catm.core.network.interceptors.LoggingInterceptor
import ru.metasharks.catm.core.network.interceptors.RequestInterceptor
import ru.metasharks.catm.core.network.request.auth.AuthRequestModifier
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorageImpl
import ru.metasharks.catm.core.network.request.csrf.CsrfRequestModifier
import ru.metasharks.catm.core.network.request.csrf.CsrfTokenStorage
import ru.metasharks.catm.core.network.socket.SocketUrlProvider
import ru.metasharks.catm.core.network.switchurl.BaseUrlSwitcher
import ru.metasharks.catm.core.network.switchurl.BaseUrlSwitcherImpl
import ru.metasharks.catm.model.api.ApiUrlProviderImpl
import ru.metasharks.catm.model.api.MediaUrlProviderImpl
import ru.metasharks.catm.model.api.SocketUrlProviderImpl
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ApiServiceModule {

    @Binds
    abstract fun bindApiUrlProvider(impl: ApiUrlProviderImpl): ApiUrlProvider

    @Binds
    abstract fun bindMediaUrlProvider(impl: MediaUrlProviderImpl): MediaUrlProvider

    @Binds
    abstract fun bindSocketUrlProvider(impl: SocketUrlProviderImpl): SocketUrlProvider

    @Binds
    abstract fun bindCsrfTokenProvider(impl: CsrfTokenStorageImpl): CsrfTokenStorage

    @Binds
    abstract fun bindAuthTokenStorage(impl: AuthTokenStorageImpl): AuthTokenStorage

    @Binds
    abstract fun bindBaseUrlSwitcher(impl: BaseUrlSwitcherImpl): BaseUrlSwitcher

    companion object {

        private const val RECONNECT_TIME_SECONDS = 15L
        private const val READ_TIME_SECONDS = 15L

        private val json = Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
        }

        @ExperimentalSerializationApi
        @Provides
        @Singleton
        fun provideJsonConverterFactory(): Converter.Factory {
            val contentType = "application/json".toMediaType()
            return json.asConverterFactory(contentType)
        }

        @Provides
        @Singleton
        fun provideCookieJar(
            cookieStorage: CookieStorage,
        ): CookieJar {
            return object : CookieJar {
                private val cookiesStore: MutableList<Cookie> = mutableListOf()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookies.forEach { cookie ->
                        cookieStorage.saveCookie(cookie)
                        cookiesStore.add(cookie)
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookiesStore
                }
            }
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            loggingInterceptor: LoggingInterceptor,
            cookieInterceptor: CookieInterceptor,
            cookieJar: CookieJar,
            csrfTokenStorage: CsrfTokenStorage,
            authTokenStorage: AuthTokenStorage,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(RECONNECT_TIME_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(
                    RequestInterceptor(
                        listOf(
                            CsrfRequestModifier(csrfTokenStorage),
                            AuthRequestModifier(authTokenStorage),
                        )
                    )
                )
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(loggingInterceptor)
                        addInterceptor(cookieInterceptor)
                    }
                }
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            apiUrlProvider: ApiUrlProvider,
            jsonConverterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(jsonConverterFactory)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl(apiUrlProvider.baseUrl)
                .build()
        }
    }
}
