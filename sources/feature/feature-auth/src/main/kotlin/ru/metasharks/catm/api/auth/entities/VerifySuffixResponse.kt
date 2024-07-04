package ru.metasharks.catm.api.auth.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySuffixResponse(
    @SerialName("match")
    val match : Boolean
)