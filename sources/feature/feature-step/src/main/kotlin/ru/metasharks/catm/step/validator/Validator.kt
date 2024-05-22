package ru.metasharks.catm.step.validator

import android.view.View
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.utils.strings.StringResWrapper

abstract class Validator<T : StepManager<*, *, *>> {

    abstract val type: PatternDataType

    abstract val warningText: StringResWrapper

    abstract fun validate(
        managerGetter: (PatternDataType) -> T,
        childGetter: (Any?) -> View
    ): Result

    class Result(
        val isValid: Boolean,
        val warning: String?,
    )
}
