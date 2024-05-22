package ru.metasharks.catm.feature.workpermit.entities.responsiblemanager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPermitUsersEnvelopeX(

    @SerialName("count")
    val count: Int,

    @SerialName("next")
    val next: String?,

    @SerialName("previous")
    val previous: String?,

    @SerialName("results")
    val results: List<WorkPermitUserX>
)
