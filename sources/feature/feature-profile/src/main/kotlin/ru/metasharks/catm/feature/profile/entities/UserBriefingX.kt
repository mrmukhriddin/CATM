package ru.metasharks.catm.feature.profile.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserBriefingX(
    @SerialName("category")
    val category: String,
    @SerialName("signed")
    val signed: Boolean
)
