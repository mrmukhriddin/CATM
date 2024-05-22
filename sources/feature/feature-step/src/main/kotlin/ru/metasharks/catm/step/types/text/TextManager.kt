package ru.metasharks.catm.step.types.text

import android.view.View
import androidx.core.widget.addTextChangedListener
import ru.metasharks.catm.feature.step.databinding.StepPatternItemTextBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.utils.StepErrorUtils
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.step.validator.Validator

class TextManager : StepManager<StepPatternItemTextBinding, String, String>(
    { StepPatternItemTextBinding.bind(it) }
) {

    override fun setValue(view: View, valueToSet: Any?) {
        val binding = binder(view)
        (valueToSet as? String)?.let {
            binding.input.setText(it)
        }
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.input.text.clear()
    }

    override fun showError(view: View, error: Validator.Result) {
        val binding = binder(view)
        StepErrorUtils.showError(error.warning, binding.root, binding.input)
    }

    override fun gatherRestoreDataFromView(view: View): String? {
        return gatherDataFromView(view)
    }

    override fun gatherDataFromView(view: View): String? {
        val binding = binder(view)
        return binding.input.text.toString().ifEmpty { null }
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.input.addTextChangedListener {
            statusListener.update(tag, !it.isNullOrBlank())
        }
        statusListener.update(tag, !binding.input.text.isNullOrBlank())
    }
}
