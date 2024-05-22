package ru.metasharks.catm.feature.profile.entities.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingLightX(
    @SerialName("message")
    val message: String,
    @SerialName("passed")
    val passed: Boolean
)
