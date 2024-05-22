package ru.metasharks.catm.feature.documents.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentX(
    @SerialName("file")
    val fileUri: String,
    @SerialName("file_size")
    val fileSize: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)
