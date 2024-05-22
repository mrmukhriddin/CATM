package ru.metasharks.catm.api.auth.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("expiry")
    val expiry: String,
    @SerialName("token")
    val token: String,
    @SerialName("user")
    val user: LoginUser
)
