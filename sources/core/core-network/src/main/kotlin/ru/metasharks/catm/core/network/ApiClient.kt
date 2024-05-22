package ru.metasharks.catm.core.network

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(private val retrofit: Retrofit) {

    fun <T> createService(apiClass: Class<T>): T = retrofit.create(apiClass)
}
