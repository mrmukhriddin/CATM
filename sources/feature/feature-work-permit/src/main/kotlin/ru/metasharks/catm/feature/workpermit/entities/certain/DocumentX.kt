package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentX(

    @SerialName("file")
    val fileUrl: String,

    @SerialName("file_size")
    val fileSize: Int,

    @SerialName("id")
    val id: Long,

    @SerialName("title")
    val title: String,

    @SerialName("type")
    val type: Int
)
