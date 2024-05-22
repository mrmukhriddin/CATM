package ru.metasharks.catm.step.types.doubletext

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDoubleTextBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString

class DoubleTextFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemDoubleTextBinding, Field.DoubleText.Input>(
    parent,
    R.layout.step_pattern_item_double_text,
    { StepPatternItemDoubleTextBinding.bind(it) },
) {

    override fun createView(prompt: Field.DoubleText.Input): View {
        super.createView(prompt)
        with(binding) {
            if (prompt.clarification == null) {
                clarification.isGone = true
            } else {
                clarification.isVisible = true
                clarification.text = prompt.clarification
            }
            root.isEnabled = prompt.isEditable
            prompt.initialValue?.let { inputFirst.setText(it.first) }
            prompt.initialValue?.let { inputSecond.setText(it.second) }
            root.label = context.getString(prompt.label)
            inputFirst.hint = context.getString(prompt.hintFirst)
            inputSecond.hint = context.getString(prompt.hintSecond)
            root.isEnabled = prompt.isEditable
        }
        return view
    }
}
