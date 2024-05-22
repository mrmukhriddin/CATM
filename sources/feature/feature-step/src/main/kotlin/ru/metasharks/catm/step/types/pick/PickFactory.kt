package ru.metasharks.catm.step.types.pick

import android.view.View
import android.view.ViewGroup
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemPickBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString

class PickFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemPickBinding, Field.Pick.Input>(
    parent,
    R.layout.step_pattern_item_pick,
    { StepPatternItemPickBinding.bind(it) }
) {

    override fun createView(prompt: Field.Pick.Input): View {
        super.createView(prompt)
        with(binding) {
            root.label = context.getString(prompt.label)
            picker.hint = context.getString(prompt.hint)
            if (prompt.isEditable) {
                picker.setOnClickListener { prompt.onSelectCallback(it) }
            } else {
                picker.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            root.isEnabled = prompt.isEditable
            prompt.initialValue?.let { pickItem ->
                picker.text = pickItem.item.value
                picker.tag = pickItem.item
            }
        }
        return view
    }
}
