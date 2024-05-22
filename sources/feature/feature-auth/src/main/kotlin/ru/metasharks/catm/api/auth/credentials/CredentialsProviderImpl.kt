package ru.metasharks.catm.api.auth.credentials

import okhttp3.Credentials
import javax.inject.Inject

internal class CredentialsProviderImpl @Inject constructor() : CredentialsProvider {

    override fun getCredentials(username: String, password: String): String {
        return Credentials.basic(username, password)
    }
}
