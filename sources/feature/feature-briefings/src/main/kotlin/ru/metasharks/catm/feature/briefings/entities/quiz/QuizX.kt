package ru.metasharks.catm.feature.briefings.entities.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizX(

    @SerialName("id")
    val id: Long,

    @SerialName("questions")
    val questions: List<QuestionX>,

    @SerialName("title")
    val title: String
)
