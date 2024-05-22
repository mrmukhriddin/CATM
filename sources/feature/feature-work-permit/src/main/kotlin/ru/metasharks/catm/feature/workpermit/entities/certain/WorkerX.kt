package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerX(

    @SerialName("id")
    val id: Long,

    @SerialName("replacement_to")
    val replacementTo: Long?,

    @SerialName("signed")
    val signed: Boolean?,

    @SerialName("signed_by_instructor")
    val signedByInstructor: Boolean?,

    @SerialName("user")
    val user: WorkerUserX,

    @SerialName("added_at")
    val addedAt: String?,
)
