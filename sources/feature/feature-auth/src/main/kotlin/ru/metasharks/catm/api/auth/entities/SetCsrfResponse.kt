package ru.metasharks.catm.api.auth.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SetCsrfResponse(
    @SerialName("details")
    val response: String,
)
