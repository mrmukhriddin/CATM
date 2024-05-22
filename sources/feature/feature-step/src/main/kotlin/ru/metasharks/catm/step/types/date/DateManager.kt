package ru.metasharks.catm.step.types.date

import android.view.View
import androidx.core.widget.addTextChangedListener
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDateBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.utils.StepErrorUtils
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.step.validator.Validator
import ru.metasharks.catm.utils.date.LocalDateUtils

class DateManager : StepManager<StepPatternItemDateBinding, String, String>(
    { StepPatternItemDateBinding.bind(it) }
) {

    override fun gatherDataFromView(view: View): String? {
        val binding = binder(view)
        if (binding.inputDate.text.isNullOrEmpty()) {
            return null
        }
        return getFinalString(binding.inputDate.text.toString())
    }

    override fun gatherRestoreDataFromView(view: View): String? {
        val binding = binder(view)
        val dateString = binding.inputDate.text.toString()
        return if (checkIfFilled(dateString)) {
            dateString
        } else {
            null
        }
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.inputDate.text.clear()
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.inputDate.addTextChangedListener {
            statusListener.update(tag, checkIfFilled(binding))
        }
        statusListener.update(tag, checkIfFilled(binding))
    }

    private fun checkIfFilled(binding: StepPatternItemDateBinding): Boolean {
        val inputDate = binding.inputDate.text.toString()
        return checkIfFilled(inputDate)
    }

    private fun getFinalString(dateString: String): String? {
        if (checkIfFilled(dateString)) {
            return LocalDateUtils.getISO8601DateString(dateString)
        }
        return null
    }

    private fun checkIfFilled(dateString: String): Boolean {
        return dateRegex.matches(dateString)
    }

    override fun showError(view: View, error: Validator.Result) {
        val binding = binder(view)
        StepErrorUtils.showError(error.warning, binding.root, binding.inputDate)
    }

    companion object {
        val dateRegex = Regex("\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d")
    }
}
