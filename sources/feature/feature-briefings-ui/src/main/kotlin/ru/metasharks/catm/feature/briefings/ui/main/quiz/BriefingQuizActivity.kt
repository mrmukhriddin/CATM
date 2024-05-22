package ru.metasharks.catm.feature.briefings.ui.main.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.ui.activity.BaseActivity
import ru.metasharks.catm.core.ui.dialog.confirmation.ConfirmationDialogBuilder
import ru.metasharks.catm.core.ui.recycler.space.DividerItemDecoration
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.databinding.ActivityBriefingQuizBinding
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuestionUi
import ru.metasharks.catm.feature.briefings.ui.entities.quiz.QuizUi
import ru.metasharks.catm.feature.briefings.ui.main.quiz.recycler.QuestionTrackerAdapter
import ru.metasharks.catm.utils.argumentDelegate
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.layoutInflater

@AndroidEntryPoint
internal class BriefingQuizActivity : BaseActivity() {

    private val viewModel: BriefingQuizViewModel by viewModels()
    private val binding: ActivityBriefingQuizBinding by viewBinding(CreateMethod.INFLATE)
    private val briefingId: Int by argumentDelegate(EXTRA_BRIEFING_ID)

    private val questionTrackerAdapter by lazy {
        QuestionTrackerAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        setupObservables()

        viewModel.initialData(briefingId)
    }

    private fun setupObservables() {
        viewModel.briefing.observe(this) { briefing ->
            binding.quizTheme.text = briefing.text
        }
        viewModel.quiz.observe(this) { quiz ->
            setQuiz(quiz)
        }
        viewModel.currentQuestion.observe(this) { payload ->
            questionTrackerAdapter.activate(payload.indexOfQuestion)
            setQuestion(payload.question, payload.isLast)
        }
        viewModel.passed.observe(this) { passed ->
            if (passed.not()) {
                ConfirmationDialogBuilder(this)
                    .setTitle("Вы не прошли тест.")
                    .setPositiveButtonText("ОК")
                    .setNegativeButtonShow(false)
                    .setOnPositiveButtonAction {
                        viewModel.exit()
                    }
                    .show()
            } else {
                ConfirmationDialogBuilder(this)
                    .setTitle("Вы прошли тест!")
                    .setNegativeButtonShow(false)
                    .setPositiveButtonText("ОК")
                    .setOnPositiveButtonAction {
                        viewModel.refreshPreviousScreen()
                        viewModel.exit()
                    }
                    .show()
            }
        }
    }

    private fun setQuiz(quiz: QuizUi) {
        with(binding) {
            actionBtn.isVisible = true
            quizName.text = quiz.title
            questionTrackerAdapter.items = quiz.questionTrackerItems
        }
        viewModel.initQuiz()
    }

    private fun setQuestion(question: QuestionUi, isLast: Boolean) {
        with(binding) {
            with(actionBtn) {
                isEnabled = false
                if (isLast) {
                    setText(R.string.btn_text_send_results)
                    setOnClickListener {
                        viewModel.answer(question, getCurrentOptionId())
                        viewModel.endQuiz()
                    }
                } else {
                    setText(R.string.btn_text_next_question)
                    setOnClickListener {
                        viewModel.answer(question, getCurrentOptionId())
                        viewModel.nextQuestion()
                    }
                }
            }
            quizQuestion.text = question.questionText
            with(quizOptions) {
                removeAllViews()
                question.options.forEach { optionUi ->
                    addView(createRadioButton(optionUi.text, optionUi.optionId))
                }
            }
        }
    }

    private fun getCurrentOptionId(): Long {
        with(binding.quizOptions) {
            val checkedId = checkedRadioButtonId
            return findViewById<View>(checkedId).tag as Long
        }
    }

    private fun RadioGroup.createRadioButton(name: String, optionId: Long): RadioButton {
        val btn = context.layoutInflater.inflate(
            R.layout.item_quiz_option,
            this,
            false
        ) as RadioButton
        setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                binding.actionBtn.isEnabled = true
            }
        }
        btn.tag = optionId
        btn.text = name
        return btn
    }

    private fun setupUi() {
        setContentView(binding.root)
        setupToolbar()
        with(binding) {
            with(quizProgressRecycler) {
                layoutManager = LinearLayoutManager(
                    this@BriefingQuizActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = questionTrackerAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        dividerTotalSize = dpToPx(SPACE_BETWEEN_TRACKER_ITEMS),
                        orientation = DividerItemDecoration.HORIZONTAL,
                        showDividersFlag = DividerItemDecoration.SHOW_MIDDLE or DividerItemDecoration.SHOW_END,
                    )
                )
            }
        }
    }

    private fun setupToolbar() {
        setToolbar(
            binding.toolbarContainer.toolbar,
            showTitle = false,
            showNavigate = true
        )
    }

    override fun onBackPressed() {
        viewModel.exit()
    }

    companion object {

        private const val SPACE_BETWEEN_TRACKER_ITEMS = 10

        private const val EXTRA_BRIEFING_ID: String = "briefing_id"

        fun createIntent(context: Context, briefingId: Int): Intent {
            return Intent(context, BriefingQuizActivity::class.java)
                .putExtra(EXTRA_BRIEFING_ID, briefingId)
        }
    }
}
