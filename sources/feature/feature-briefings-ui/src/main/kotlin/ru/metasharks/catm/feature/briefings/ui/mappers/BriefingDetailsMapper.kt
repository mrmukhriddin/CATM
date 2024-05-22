package ru.metasharks.catm.feature.briefings.ui.mappers

import android.content.Context
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.ui.R
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingDetailsUi
import ru.metasharks.catm.utils.strings.getFileTypeAndSizeString
import javax.inject.Inject

internal class BriefingDetailsMapper @Inject constructor(
    private val context: Context,
) {

    fun map(
        item: BriefingX,
        signed: Boolean? = null,
        quizPassed: Boolean? = null
    ): BriefingDetailsUi {
        return BriefingDetailsUi(
            text = item.title,
            brId = item.id,
            categoryId = item.category,
            typeId = item.type,
            content = item.content,
            fileData = createFileData(item),
            quizId = item.quiz,
            bottomActions = createBottomActions(item, signed, quizPassed),
        )
    }

    private fun createFileData(item: BriefingX): BriefingDetailsUi.FileData? {
        if (item.file == null || item.fileSize == null || item.fileName == null) {
            return null
        }
        val url = item.file!!
        return BriefingDetailsUi.FileData(
            url = url,
            sizeAndExtension = getFileTypeAndSizeString(url, item.fileSize!!),
            name = item.fileName!!
        )
    }

    @Suppress("ComplexMethod")
    private fun createBottomActions(
        item: BriefingX,
        signed: Boolean?,
        passed: Boolean?
    ): BriefingDetailsUi.BottomActions {
        val hasQuiz = item.quiz != null
        val basePassed = if (hasQuiz) passed else null
        val quizPassed = basePassed ?: item.isQuizPassed
        val briefingSigned =
            signed ?: (item.userBriefingSignedAt != null)
        // пока не получилось норм подписать документ, чтобы потом с сервера получить обновленную сущность
        // поэтому тут просто извне передается флаг, что мол подписано
        return when {
            !hasQuiz && !quizPassed && !briefingSigned -> {
                BriefingDetailsUi.BottomActions(
                    bottomSignButtonVisible = true,
                    bottomSignButtonEnabled = true,
                )
            }
            !hasQuiz && !quizPassed && briefingSigned -> {
                BriefingDetailsUi.BottomActions(
                    bottomResultsText = context.getString(R.string.briefings_results_signed),
                )
            }
            hasQuiz && !quizPassed && !briefingSigned -> {
                BriefingDetailsUi.BottomActions(
                    bottomQuizButtonVisible = true,
                    bottomSignButtonVisible = true,
                    bottomSignButtonEnabled = false,
                )
            }
            hasQuiz && quizPassed && !briefingSigned -> {
                BriefingDetailsUi.BottomActions(
                    bottomResultsText = context.getString(R.string.briefings_results_quiz_done),
                    bottomSignButtonVisible = true,
                    bottomSignButtonEnabled = true,
                )
            }
            hasQuiz && quizPassed && briefingSigned -> {
                BriefingDetailsUi.BottomActions(
                    bottomResultsText = context.getString(R.string.briefings_results_all_done),
                )
            }
            else -> throw IllegalStateException("Illegal flags case: hasQuiz = $hasQuiz; quizPassed = $quizPassed; briefingSigned = $briefingSigned")
        }
    }
}
