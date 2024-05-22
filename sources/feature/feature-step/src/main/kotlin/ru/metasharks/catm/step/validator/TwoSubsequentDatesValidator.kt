package ru.metasharks.catm.step.validator

import android.view.View
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.types.dateandtime.DateAndTimeManager
import ru.metasharks.catm.utils.date.LocalDateUtils
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.strings.StringResWrapper

class TwoSubsequentDatesValidator(
    private val startTimeTag: String,
    private val endTimeTag: String,
    override val type: PatternDataType = PatternDataType.DATE_AND_TIME,
    private val startTimeType: PatternDataType = type,
    private val startTimeManagerClass: Class<out StepManager<*, *, *>> = DateAndTimeManager::class.java,
    private val endTimeType: PatternDataType = type,
    private val endTimeManagerClass: Class<out StepManager<*, *, *>> = DateAndTimeManager::class.java,
    warning: StringResWrapper,
) : Validator<StepManager<*, *, *>>() {

    override val warningText: StringResWrapper = warning

    override fun validate(
        managerGetter: (PatternDataType) -> StepManager<*, *, *>,
        childGetter: (Any?) -> View
    ): Result {
        val startTimeManager = startTimeManagerClass.cast(managerGetter(startTimeType))
        val endTimeManager = endTimeManagerClass.cast(managerGetter(endTimeType))
        val startTimeView = childGetter(startTimeTag)
        val endTimeView = childGetter(endTimeTag)
        val context = startTimeView.context
        val startTime = startTimeManager.gatherDataFromView(startTimeView) as String?
        val endTime = endTimeManager.gatherDataFromView(endTimeView) as String?
        if (startTime == null || endTime == null) {
            return Result(false, context.getString(warningText))
        }
        val startLocalDateTime = LocalDateUtils.parseISO8601toLocalDateTime(startTime)
        val endLocalDateTime = LocalDateUtils.parseISO8601toLocalDateTime(endTime)

        val isValid = startLocalDateTime < endLocalDateTime
        val result = Result(isValid, context.getString(warningText))
        if (result.isValid.not()) {
            endTimeManager.showError(endTimeView, result)
        }
        return result
    }
}
