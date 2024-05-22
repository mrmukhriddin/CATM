package ru.metasharks.catm.feature.workpermit.entities.options

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OptionX(

    @SerialName("id")
    val id: Long,

    @SerialName("value")
    val value: String
)
