package ru.metasharks.catm.feature.workpermit.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPermitsEnvelopeX(
    @SerialName("count")
    val count: Int,
    @SerialName("next")
    val next: String?,
    @SerialName("previous")
    val previous: String?,
    @SerialName("results")
    val results: List<WorkPermitX>
)
