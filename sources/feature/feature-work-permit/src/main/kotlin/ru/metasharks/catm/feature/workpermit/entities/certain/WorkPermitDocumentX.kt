package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPermitDocumentX(

    @SerialName("file")
    val fileUrl: String,

    @SerialName("file_name")
    val fileName: String,

    @SerialName("file_size")
    val fileSize: Int,

    @SerialName("id")
    val id: Long,

    @SerialName("work_permit")
    val workPermit: Long
)
