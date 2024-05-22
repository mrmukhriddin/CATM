package ru.metasharks.catm.feature.documents.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentTypeX(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)
