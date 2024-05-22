package ru.metasharks.catm.feature.createworkpermit.ui.steps

import ru.metasharks.catm.feature.workpermit.entities.options.OptionX
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.PatternDataType
import ru.metasharks.catm.step.StepPatternLayout
import ru.metasharks.catm.step.entities.StepPatternRestoreData
import ru.metasharks.catm.utils.strings.StringResWrapper
import javax.inject.Inject

class StepMapper @Inject constructor() {

    fun mapOptionsToUi(
        options: List<OptionX>,
        tagPrefix: String? = null,
        restoreData: StepPatternRestoreData? = null
    ): List<StepPatternLayout.CreateItemData> {
        return options.map { mapOptionToUi(it, tagPrefix, restoreData) }
    }

    fun mapOptionToUi(
        option: OptionX,
        tagPrefix: String? = null,
        restoreData: StepPatternRestoreData? = null
    ): StepPatternLayout.CreateItemData {
        val tag = tagPrefix?.let { tagPrefix + option.id.toString() } ?: option.id.toString()
        return StepPatternLayout.CreateItemData(
            itemType = PatternDataType.CHECK,
            tag = tag,
            prompt = Field.Check.Input(
                description = StringResWrapper(option.value),
                option.id,
                initialValue = restoreData?.byTag(tag)
            )
        )
    }
}
