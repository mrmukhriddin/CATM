package ru.metasharks.catm.feature.workpermit.entities.worktype

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkTypeX(

    @SerialName("id")
    val id: Long,

    @SerialName("position")
    val position: Int,

    @SerialName("title")
    val title: String
)
