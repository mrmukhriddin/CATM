package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignerX(

    @SerialName("role")
    val role: String,

    @SerialName("role_label")
    val roleLabel: String,

    @SerialName("user")
    val user: SignerUserX,

    @SerialName("signed")
    val signed: Boolean?
)
