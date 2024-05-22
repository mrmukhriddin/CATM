package ru.metasharks.catm.step.types.dateandtime

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDateAndTimeBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class DateAndTimeFactory(
    parent: ViewGroup
) : StepFactory<StepPatternItemDateAndTimeBinding, Field.DateAndTime.Input>(
    parent,
    R.layout.step_pattern_item_date_and_time,
    { StepPatternItemDateAndTimeBinding.bind(it) }
) {

    private val dateMask: MaskImpl
        get() {
            val slots = UnderscoreDigitSlotsParser().parseSlots("__.__.____")
            return MaskImpl.createTerminated(slots)
        }

    private val timeMask: MaskImpl
        get() {
            val slots = UnderscoreDigitSlotsParser().parseSlots("__:__")
            return MaskImpl.createTerminated(slots)
        }

    private fun EditText.installDateMask() {
        installMask(dateMask)
    }

    private fun EditText.installTimeMask() {
        installMask(timeMask)
    }

    private fun EditText.installMask(maskImpl: MaskImpl) {
        val watcher: FormatWatcher = MaskFormatWatcher(maskImpl)
        watcher.installOn(this) // install on any TextView
    }

    override fun createView(prompt: Field.DateAndTime.Input): View {
        super.createView(prompt)
        with(binding) {
            root.label = context.getString(prompt.label)
            prompt.initialValue?.let { (date, time) ->
                inputDate.setText(date)
                inputTime.setText(time)
            }
            inputDate.isEnabled = prompt.canEditDate
            inputDate.installDateMask()
            inputTime.installTimeMask()
        }
        return view
    }
}
