package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnitX(

    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String
)