package ru.metasharks.catm.step.types.header

import android.view.View
import android.view.ViewGroup
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemHeaderBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory

class HeaderFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemHeaderBinding, Field.Header.Input>(
    parent,
    R.layout.step_pattern_item_header,
    { StepPatternItemHeaderBinding.bind(it) }
) {

    override fun createView(prompt: Field.Header.Input): View {
        super.createView(prompt)
        with(binding) {
            root.text = prompt.text
        }
        return view
    }
}
