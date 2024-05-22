package ru.metasharks.catm.feature.profile.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProtectiveEquipmentCardX(
    @SerialName("id")
    val id: Int,
    @SerialName("expiration_date")
    val expirationDate: String,
    @SerialName("file")
    val fileUri: String?,
    @SerialName("number")
    val number: String,
)
