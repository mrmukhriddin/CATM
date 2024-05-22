package ru.metasharks.catm.feature.briefings.ui.entities.quiz

data class QuizUi(

    val quizId: Long,

    val title: String,

    val questions: List<QuestionUi>,
) {

    val questionTrackerItems: List<QuestionTrackerItem> =
        questions.mapIndexed { index, _ ->
            QuestionTrackerItem(
                index = index + 1,
                isCurrent = if (index == 0) {
                    true
                } else {
                    null
                }
            )
        }
}
