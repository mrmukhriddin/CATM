package ru.metasharks.catm.step.types.check

import android.view.View
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.feature.step.databinding.StepPatternItemCheckBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.validator.StatusListener

class CheckManager : StepManager<StepPatternItemCheckBinding, Long?, Boolean>(
    { StepPatternItemCheckBinding.bind(it) }
) {

    override fun gatherDataFromView(view: View): Long? {
        val binding = binder(view)
        return if (binding.checkBox.isChecked) {
            binding.checkBox.getTag(R.id.key_value) as Long
        } else null
    }

    override fun gatherRestoreDataFromView(view: View): Boolean {
        val binding = binder(view)
        return binding.checkBox.isChecked
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.checkBox.isChecked = false
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            statusListener.update(tag, isChecked)
        }
        statusListener.update(tag, binding.checkBox.isChecked)
    }
}
