package ru.metasharks.catm.step.validator

import android.view.View
import org.joda.time.LocalDateTime
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.types.dateandtime.DateAndTimeManager
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

class DateAfterTodayValidator(
    private val dateTag: String,
    warning: StringResWrapper,
) : Validator<DateAndTimeManager>() {

    override val type: PatternDataType = PatternDataType.DATE_AND_TIME
    override val warningText: StringResWrapper = warning

    override fun validate(
        managerGetter: (PatternDataType) -> DateAndTimeManager,
        childGetter: (Any?) -> View
    ): Result {
        val manager = managerGetter(type)
        val startTimeView = childGetter(dateTag)
        val context = startTimeView.context
        val resultWarningText = context.getString(warningText)
        val dateTimeString =
            manager.gatherDataFromView(startTimeView) ?: return Result(false, resultWarningText)
        val dateTime = LocalDateUtils.parseISO8601toLocalDateTime(dateTimeString)
        val isValid = dateTime.isAfter(LocalDateTime.now())
        val result = Result(isValid, resultWarningText)
        if (result.isValid.not()) {
            manager.showError(startTimeView, result)
        }
        return result
    }
}
