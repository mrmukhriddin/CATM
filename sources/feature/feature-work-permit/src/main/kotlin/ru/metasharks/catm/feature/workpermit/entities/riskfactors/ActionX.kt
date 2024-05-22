package ru.metasharks.catm.feature.workpermit.entities.riskfactors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActionX(

    @SerialName("id")
    val id: Int?,

    @SerialName("text")
    val text: String,

    @SerialName("subactions")
    val subactions: List<String>? = null,
)
