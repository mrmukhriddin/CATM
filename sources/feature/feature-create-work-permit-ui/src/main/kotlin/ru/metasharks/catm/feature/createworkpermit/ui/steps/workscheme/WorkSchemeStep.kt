package ru.metasharks.catm.feature.createworkpermit.ui.steps.workscheme

import ru.metasharks.catm.step.entities.BaseStep
import ru.metasharks.catm.utils.strings.StringResWrapper

data class WorkSchemeStep(
    override val number: Int,
    override val description: StringResWrapper,
) : BaseStep(number, description) {

    override val tag: String = TAG

    companion object {

        const val TAG = "step_work_scheme"
    }
}
