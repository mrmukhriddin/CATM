package ru.metasharks.catm.step.types.pick

import android.view.View
import androidx.core.widget.addTextChangedListener
import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.feature.step.databinding.StepPatternItemPickBinding
import ru.metasharks.catm.step.Field
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.validator.StatusListener

class PickManager : StepManager<StepPatternItemPickBinding, Long, Field.Pick.InitialValue>(
    { StepPatternItemPickBinding.bind(it) }
) {

    override fun gatherDataFromView(view: View): Long? {
        val binding = binder(view)
        val item = binding.picker.tag as PickItemDialog.ItemUi?
        return item?.entityId
    }

    override fun gatherRestoreDataFromView(view: View): Field.Pick.InitialValue? {
        val binding = binder(view)
        return if (binding.picker.text.isNullOrBlank().not()) {
            Field.Pick.InitialValue(
                binding.picker.tag as PickItemDialog.ItemUi
            )
        } else {
            null
        }
    }

    override fun setValue(view: View, valueToSet: Any?) {
        val binding = binder(view)
        val item = valueToSet as PickItemDialog.ItemUi? ?: run {
            binding.picker.text
            return
        }
        binding.picker.text = item.value
        binding.picker.tag = item
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.picker.text = ""
        binding.picker.tag = null
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.picker.addTextChangedListener {
            statusListener.update(tag, !it.isNullOrBlank())
        }
        statusListener.update(tag, !binding.picker.text.isNullOrBlank())
    }
}
