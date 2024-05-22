package ru.metasharks.catm.feature.workpermit.entities.certain.upload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadAdditionalDocumentResponseX(

    @SerialName("id")
    val id: Long,

    @SerialName("file")
    val fileUrl: String,

    @SerialName("file_name")
    val fileName: String,

    @SerialName("file_size")
    val fileSize: Int,

    @SerialName("work_permit")
    val workPermit: Int
)
