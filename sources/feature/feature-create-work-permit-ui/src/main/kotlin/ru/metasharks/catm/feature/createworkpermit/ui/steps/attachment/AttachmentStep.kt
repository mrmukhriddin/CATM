package ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment

import ru.metasharks.catm.step.entities.BaseStep
import ru.metasharks.catm.utils.strings.StringResWrapper

data class AttachmentStep(
    override val number: Int,
    override val description: StringResWrapper,
) : BaseStep(number, description) {

    override val tag: String = TAG

    companion object {

        const val TAG = "step_attachment"
    }
}
