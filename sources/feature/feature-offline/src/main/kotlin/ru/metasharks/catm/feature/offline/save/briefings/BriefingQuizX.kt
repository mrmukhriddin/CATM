package ru.metasharks.catm.feature.offline.save.briefings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuizX

@Serializable
data class BriefingQuizX(

    @SerialName("briefing")
    val briefingX: BriefingX,

    @SerialName("quiz")
    val quizX: QuizX?,
)
