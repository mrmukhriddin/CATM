package ru.metasharks.catm.api.auth.credentials

interface CredentialsProvider {

    fun getCredentials(username: String, password: String): String
}
