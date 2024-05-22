package ru.metasharks.catm.step.types.check

import android.view.View
import android.view.ViewGroup
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemCheckBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString

class CheckFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemCheckBinding, Field.Check.Input>(
    parent,
    R.layout.step_pattern_item_check,
    { StepPatternItemCheckBinding.bind(it) }
) {

    override fun createView(prompt: Field.Check.Input): View {
        super.createView(prompt)
        with(binding) {
            checkBox.text = context.getString(prompt.description)
            checkBox.setTag(R.id.key_value, prompt.valueId)
            prompt.initialValue?.let {
                checkBox.isChecked = it
            }
        }
        return view
    }
}
