package ru.metasharks.catm.feature.briefings.entities.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoiceX(

    @SerialName("id")
    val id: Long,

    @SerialName("text")
    val text: String
)
