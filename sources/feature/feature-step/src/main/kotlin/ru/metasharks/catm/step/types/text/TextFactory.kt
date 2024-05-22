package ru.metasharks.catm.step.types.text

import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemTextBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepFactory
import ru.metasharks.catm.utils.getString

class TextFactory(
    parent: ViewGroup,
) : StepFactory<StepPatternItemTextBinding, Field.Text.Input>(
    parent,
    R.layout.step_pattern_item_text,
    { StepPatternItemTextBinding.bind(it) },
) {

    override fun createView(prompt: Field.Text.Input): View {
        super.createView(prompt)
        with(binding) {
            if (prompt.oneLine) {
                input.maxLines = 1
                input.setLines(1)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.gravity = Gravity.CENTER_VERTICAL
            } else {
                input.maxLines = Int.MAX_VALUE
                input.setLines(MULTILINE_TEXT_LINES)
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                input.gravity = Gravity.TOP
            }
            if (prompt.clarification == null) {
                clarification.isGone = true
            } else {
                clarification.isVisible = true
                clarification.text = context.getString(prompt.clarification)
            }
            prompt.initialValue?.let { input.setText(it) }
            root.label = context.getString(prompt.label)
            input.hint = context.getString(prompt.hint)
            root.isEnabled = prompt.isEditable
        }
        return view
    }

    companion object {

        private const val MULTILINE_TEXT_LINES = 3
    }
}
