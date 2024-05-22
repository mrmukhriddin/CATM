package ru.metasharks.catm.step.validator

import android.view.View
import org.joda.time.LocalDateTime
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.types.dateandtime.DateAndTimeManager
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

class DateAfterDayValidator(
    private val dateTag: String,
    warning: StringResWrapper,
    private val dateToCompare: LocalDateTime,
    override val type: PatternDataType = PatternDataType.DATE_AND_TIME,
    private val managerClass: Class<out StepManager<*, *, *>> = DateAndTimeManager::class.java,
) : Validator<StepManager<*, *, *>>() {

    override val warningText: StringResWrapper = warning

    override fun validate(
        managerGetter: (PatternDataType) -> StepManager<*, *, *>,
        childGetter: (Any?) -> View
    ): Result {
        val manager = managerClass.cast(managerGetter(type))
        val startTimeView = childGetter(dateTag)
        val context = startTimeView.context
        val resultedWarning = context.getString(warningText)
        val dateTimeString =
            manager.gatherDataFromView(startTimeView) as String? ?: return Result(
                false,
                resultedWarning
            )
        val targetDate = LocalDateUtils.parseLocalDateTime(dateTimeString)
        val isValid = targetDate > dateToCompare

        val result = Result(isValid, resultedWarning)

        if (result.isValid.not()) {
            manager.showError(startTimeView, result)
        }

        return result
    }
}
