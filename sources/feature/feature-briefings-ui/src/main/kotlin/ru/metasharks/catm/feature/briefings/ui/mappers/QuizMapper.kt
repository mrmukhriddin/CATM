package ru.metasharks.catm.feature.briefings.ui.mappers

import ru.metasharks.catm.feature.briefings.entities.quiz.ChoiceX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuestionX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuizX
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.OptionUi
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuestionUi
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuizUi
import javax.inject.Inject

class QuizMapper @Inject constructor() {

    fun mapQuiz(item: QuizX): QuizUi {
        return QuizUi(
            quizId = item.id,
            title = item.title,
            questions = item.questions.map(::mapQuestion)
        )
    }

    fun mapQuestion(item: QuestionX): QuestionUi {
        return QuestionUi(
            questionId = item.id,
            questionText = item.text,
            options = item.choices.map(::mapChoice)
        )
    }

    fun mapChoice(item: ChoiceX): OptionUi {
        return OptionUi(
            optionId = item.id,
            text = item.text
        )
    }
}
