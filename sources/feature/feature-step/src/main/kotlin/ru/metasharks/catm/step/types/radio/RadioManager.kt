package ru.metasharks.catm.step.types.radio

import android.view.View
import android.widget.RadioButton
import ru.metasharks.catm.feature.step.databinding.StepPatternItemRadioBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.validator.StatusListener

class RadioManager : StepManager<StepPatternItemRadioBinding, String, String>(
    { StepPatternItemRadioBinding.bind(it) }
) {

    override fun gatherDataFromView(view: View): String? {
        val binding = binder(view)
        val checkedId = binding.radio.checkedRadioButtonId
        if (checkedId == -1) {
            return null
        }
        val btn = binding.radio.findViewById<RadioButton>(checkedId)
        return btn.tag as String
    }

    override fun gatherRestoreDataFromView(view: View): String? {
        val binding = binder(view)
        val btn = binding.radio.findViewById<RadioButton>(binding.radio.checkedRadioButtonId)
        return btn.tag as String?
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.radio.setOnCheckedChangeListener { group, checkedId ->
            statusListener.update(tag, checkedId != 1)
        }
        statusListener.update(tag, binding.radio.checkedRadioButtonId != -1)
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.radio.clearCheck()
    }
}
