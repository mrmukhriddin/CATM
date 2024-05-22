package ru.metasharks.catm.feature.feedback.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendFeedbackRequestX(

    @SerialName("theme_name")
    val theme: String,

    @SerialName("email")
    val email: String,

    @SerialName("message")
    val message: String,
)
