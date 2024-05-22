package ru.metasharks.catm.step.types.doubletext

import android.view.View
import androidx.core.widget.addTextChangedListener
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDoubleTextBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.validator.StatusListener

class DoubleTextManager :
    StepManager<StepPatternItemDoubleTextBinding, Pair<String?, String?>, Pair<String?, String?>>(
        { StepPatternItemDoubleTextBinding.bind(it) }
    ) {

    override fun gatherRestoreDataFromView(view: View): Pair<String?, String?>? {
        return gatherDataFromView(view)
    }

    override fun gatherDataFromView(view: View): Pair<String?, String?>? {
        val binding = binder(view)
        val firstValue = binding.inputFirst.text.toString().ifBlank { null }
        val secondValue = binding.inputSecond.text.toString().ifBlank { null }
        if (firstValue.isNullOrBlank() && secondValue.isNullOrBlank()) {
            return null
        }
        return firstValue to secondValue
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.inputFirst.addTextChangedListener {
            statusListener.update(tag, isFilled(binding))
        }
        binding.inputSecond.addTextChangedListener {
            statusListener.update(tag, isFilled(binding))
        }
        statusListener.update(
            tag,
            isFilled(binding)
        )
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.inputFirst.text.clear()
        binding.inputSecond.text.clear()
    }

    private fun isFilled(binding: StepPatternItemDoubleTextBinding): Boolean {
        return !binding.inputFirst.text.isNullOrBlank() && !binding.inputSecond.text.isNullOrBlank()
    }
}
