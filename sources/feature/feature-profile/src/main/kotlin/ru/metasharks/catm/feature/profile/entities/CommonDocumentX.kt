package ru.metasharks.catm.feature.profile.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonDocumentX(

    @SerialName("date")
    val date: String,

    @SerialName("document")
    val document: String,

    @SerialName("file_name")
    val fileName: String,

    @SerialName("file_size")
    val fileSize: Int,

    @SerialName("time")
    val time: String,

    @SerialName("user")
    val user: Int
)
