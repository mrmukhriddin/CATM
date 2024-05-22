package ru.metasharks.catm.feature.briefings.entities.quiz.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PassQuizRequestX(

    @SerialName("id")
    val quizId: Long,

    @SerialName("questions")
    val questions: List<QuestionRequestX>
)

@Serializable
data class QuestionRequestX(

    @SerialName("id")
    val questionId: Long,

    @SerialName("choices")
    val choices: List<ChoiceRequestX>,
)

@Serializable
data class ChoiceRequestX(

    @SerialName("id")
    val choiceId: Long,

    @SerialName("is_checked")
    val isChecked: Boolean,
)
