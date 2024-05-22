package ru.metasharks.catm.feature.briefings.ui.main.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.feature.briefings.entities.quiz.QuizX
import ru.metasharks.catm.feature.briefings.entities.quiz.request.ChoiceRequestX
import ru.metasharks.catm.feature.briefings.entities.quiz.request.PassQuizRequestX
import ru.metasharks.catm.feature.briefings.entities.quiz.request.QuestionRequestX
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuestionUi
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuizUi
import ru.metasharks.catm.feature.briefings.ui.mappers.BriefingMapper
import ru.metasharks.catm.feature.briefings.ui.mappers.QuizMapper
import ru.metasharks.catm.feature.briefings.ui.screens.BriefingQuizScreen
import ru.metasharks.catm.feature.briefings.usecase.GetBriefingsUseCase
import ru.metasharks.catm.feature.briefings.usecase.GetQuizUseCase
import ru.metasharks.catm.feature.briefings.usecase.PassQuizUseCase
import ru.metasharks.catm.feature.offline.pending.PendingActionsRepository
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.offline.save.briefings.GetBriefingOfflineDataUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class BriefingQuizViewModel @Inject constructor(
    private val appRouter: ApplicationRouter,
    private val getQuizUseCase: GetQuizUseCase,
    private val getBriefingsUseCase: GetBriefingsUseCase,
    private val getBriefingOfflineDataUseCase: GetBriefingOfflineDataUseCase,
    private val passQuizUseCase: PassQuizUseCase,
    private val quizMapper: QuizMapper,
    private val briefingMapper: BriefingMapper,
    private val offlineModeProvider: OfflineModeProvider,
    private val pendingActionsRepository: PendingActionsRepository,
) : ViewModel() {

    private val _briefing = MutableLiveData<BriefingUi>()
    val briefing: LiveData<BriefingUi> = _briefing

    private val _quiz = MutableLiveData<QuizUi>()
    val quiz: LiveData<QuizUi> = _quiz

    private val questions
        get() = requireNotNull(_quiz.value).questions

    private val _currentQuestion = MutableLiveData<CurrentQuestionPayload>()
    val currentQuestion: LiveData<CurrentQuestionPayload> = _currentQuestion

    private val _passed = MutableLiveData<Boolean>()
    val passed: LiveData<Boolean> = _passed

    private var questionToChoiceMap: MutableMap<Long, Long> = mutableMapOf()

    private var briefingId: Int = 0

    private var currentQuestionIndex = -1
        set(value) {
            field = value
            if (field == -1) {
                return
            }
            _currentQuestion.value =
                CurrentQuestionPayload(
                    question = questions[currentQuestionIndex],
                    indexOfQuestion = currentQuestionIndex,
                    isLast = currentQuestionIndex == questions.lastIndex
                )
        }

    fun initialData(briefingId: Int) {
        this.briefingId = briefingId
        if (offlineModeProvider.isInOnlineMode) {
            getQuizOnline()
        } else {
            getQuizOffline()
        }
            .map(quizMapper::mapQuiz)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { quiz ->
                    _quiz.value = quiz
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun getQuizOnline(): Single<QuizX> {
        return getBriefingsUseCase(fromCache = true)
            .flatMap { briefings ->
                val briefing = briefings.first { it.id == briefingId }
                _briefing.postValue(briefingMapper.map(briefing))
                getQuizUseCase(requireNotNull(briefing.quiz))
            }
    }

    private fun getQuizOffline(): Single<QuizX> {
        return getBriefingOfflineDataUseCase()
            .switchIfEmpty(Single.error(IllegalStateException("Quiz should be at this point")))
            .map { briefingOfflineData ->
                val briefings = briefingOfflineData.briefings
                requireNotNull(briefings.first { it.briefingX.id == briefingId }.quizX)
            }
    }

    fun exit() {
        appRouter.exit()
    }

    fun nextQuestion() {
        currentQuestionIndex++
    }

    fun initQuiz() {
        currentQuestionIndex = 0
    }

    fun endQuiz() {
        val quiz = requireNotNull(quiz.value)
        val request = PassQuizRequestX(
            quizId = quiz.quizId,
            questions = questions.map { question ->
                QuestionRequestX(
                    questionId = question.questionId,
                    choices = question.options.map { option ->
                        ChoiceRequestX(
                            choiceId = option.optionId,
                            isChecked = questionToChoiceMap[question.questionId] == option.optionId
                        )
                    }
                )
            }
        )
        if (offlineModeProvider.isInOnlineMode) {
            endQuizOnline(request)
        } else {
            endQuizOffline(request)
        }
    }

    private fun endQuizOnline(request: PassQuizRequestX) {
        passQuizUseCase(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { passed ->
                    _passed.value = passed
                }, {
                    Timber.e(it)
                }
            )
    }

    private fun endQuizOffline(request: PassQuizRequestX) {
        pendingActionsRepository.savePendingAction(
            PendingActionPayload.PassQuiz(
                request
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    refreshPreviousScreen()
                    exit()
                }, {
                    Timber.e(it)
                }
            )
    }

    fun answer(question: QuestionUi, currentOptionId: Long) {
        questionToChoiceMap[question.questionId] = currentOptionId
    }

    fun refreshPreviousScreen() {
        appRouter.sendResultBy(BriefingQuizScreen.KEY, true)
    }

    data class CurrentQuestionPayload(
        val question: QuestionUi,
        val indexOfQuestion: Int,
        val isLast: Boolean
    )
}
