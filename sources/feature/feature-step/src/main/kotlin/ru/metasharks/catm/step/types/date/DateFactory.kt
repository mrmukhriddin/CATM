package ru.metasharks.catm.step.types.date

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDateBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class DateFactory(parent: ViewGroup) : StepFactory<StepPatternItemDateBinding, Field.Date.Input>(
    parent,
    R.layout.step_pattern_item_date,
    { StepPatternItemDateBinding.bind(it) }
) {

    private val dateMask: MaskImpl
        get() {
            val slots = UnderscoreDigitSlotsParser().parseSlots("__.__.____")
            return MaskImpl.createTerminated(slots)
        }

    private fun EditText.installDateMask() {
        installMask(dateMask)
    }

    private fun EditText.installMask(maskImpl: MaskImpl) {
        val watcher: FormatWatcher = MaskFormatWatcher(maskImpl)
        watcher.installOn(this) // install on any TextView
    }

    override fun createView(prompt: Field.Date.Input): View {
        super.createView(prompt)
        with(binding) {
            root.label = context.getString(prompt.label)
            root.isEnabled = prompt.canEditDate
            prompt.initialValue?.let { date ->
                inputDate.setText(date)
            }
            inputDate.installDateMask()
        }
        return view
    }
}
