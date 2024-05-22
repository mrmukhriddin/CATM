package ru.metasharks.catm.feature.profile.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingX(
    @SerialName("id")
    val id: Int,
    @SerialName("category_id")
    val categoryId: Int,
    @SerialName("expiration_date")
    val expirationDate: String,
    @SerialName("file")
    val fileUri: String,
    @SerialName("number")
    val number: String,
    @SerialName("new_file_key")
    val newFileKey: String? = null,
)
