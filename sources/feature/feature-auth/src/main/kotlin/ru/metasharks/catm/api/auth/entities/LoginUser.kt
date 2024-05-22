package ru.metasharks.catm.api.auth.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginUser(
    @SerialName("username")
    val username: String
)
