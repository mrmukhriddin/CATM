package ru.metasharks.catm.feature.briefings.entities.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionX(

    @SerialName("id")
    val id: Long,

    @SerialName("choices")
    val choices: List<ChoiceX>,

    @SerialName("text")
    val text: String
)
