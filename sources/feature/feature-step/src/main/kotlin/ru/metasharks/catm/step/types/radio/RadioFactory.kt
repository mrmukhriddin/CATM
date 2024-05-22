package ru.metasharks.catm.step.types.radio

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemRadioBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.getString
import ru.metasharks.catm.utils.layoutInflater
import ru.metasharks.catm.utils.setDefiniteMargin

class RadioFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemRadioBinding, Field.Radio.Input>(
    parent,
    R.layout.step_pattern_item_radio,
    { StepPatternItemRadioBinding.bind(it) }
) {

    override fun createView(prompt: Field.Radio.Input): View {
        super.createView(prompt)
        with(binding) {
            root.label = context.getString(prompt.label)
            radio.orientation = RadioGroup.HORIZONTAL

            prompt.radioValues.forEachIndexed { index, (name, tag) ->
                val btn = radio.createRadioButton(context.getString(name), tag)
                if (index != 0) {
                    btn.setDefiniteMargin(left = context.dpToPx(DISTANCE_BETWEEN_BUTTONS))
                }
                radio.addView(btn)
                prompt.initialValue?.let {
                    if (it == tag) {
                        btn.isChecked = true
                    }
                }
            }
        }
        return view
    }

    private fun RadioGroup.createRadioButton(name: String, tag: String): RadioButton {
        val btn = context.layoutInflater.inflate(
            ru.metasharks.catm.core.ui.R.layout.radio_button,
            this,
            false
        ) as RadioButton
        btn.tag = tag
        btn.text = name
        return btn
    }

    companion object {
        private const val DISTANCE_BETWEEN_BUTTONS = 36
    }
}
