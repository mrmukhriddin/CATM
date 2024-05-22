package ru.metasharks.catm.api.auth.entities.request

import kotlinx.serialization.Serializable

@Serializable
class RequestLoginData(
    val username: String,
    val password: String,
)
