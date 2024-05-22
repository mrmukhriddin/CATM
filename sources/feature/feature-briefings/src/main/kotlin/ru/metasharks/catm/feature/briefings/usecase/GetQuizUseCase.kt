package ru.metasharks.catm.feature.briefings.usecase

import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.entities.quiz.ChoiceX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuestionX
import ru.metasharks.catm.feature.briefings.entities.quiz.QuizX
import ru.metasharks.catm.feature.briefings.services.BriefingsApi
import javax.inject.Inject

fun interface GetQuizUseCase {

    operator fun invoke(id: Long): Single<QuizX>
}

internal class GetQuizUseCaseImpl @Inject constructor(
    private val api: BriefingsApi,
) : GetQuizUseCase {

    override fun invoke(id: Long): Single<QuizX> = api.getQuiz(id)
}

internal class GetQuizUseCaseMock @Inject constructor() : GetQuizUseCase {

    override fun invoke(id: Long): Single<QuizX> = Single.just(
        QuizX(
            id = 1L,
            questions = listOf(
                QuestionX(
                    id = 1,
                    choices = listOf(
                        ChoiceX(
                            id = 2,
                            text = "Да"
                        ),
                        ChoiceX(
                            id = 3,
                            text = "А может ты?"
                        ),
                    ),
                    text = "ты пидор?"
                ),
                QuestionX(
                    id = 4,
                    choices = listOf(
                        ChoiceX(
                            id = 5,
                            text = "Да"
                        ),
                        ChoiceX(
                            id = 6,
                            text = "Я люблю сидор, так что да, я дад я дя да"
                        ),
                    ),
                    text = "ты сидор?"
                ),
                QuestionX(
                    id = 7,
                    choices = listOf(
                        ChoiceX(
                            id = 8,
                            text = "Да"
                        ),
                        ChoiceX(
                            id = 9,
                            text = "ок, вопросов больше не имею"
                        ),
                    ),
                    text = "Мне хватит буквально три вопроса, для теста, так что ок кок?"
                ),
            ),
            title = "Моковый коковый тест, чтобы протестировать залупу лупу. Джекпот"
        )
    )
}
