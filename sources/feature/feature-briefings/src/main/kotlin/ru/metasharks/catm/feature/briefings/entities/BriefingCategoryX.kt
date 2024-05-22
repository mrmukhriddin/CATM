package ru.metasharks.catm.feature.briefings.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BriefingCategoryX(
    @SerialName("id")
    val id: Int,

    @SerialName("value")
    val value: String
)
