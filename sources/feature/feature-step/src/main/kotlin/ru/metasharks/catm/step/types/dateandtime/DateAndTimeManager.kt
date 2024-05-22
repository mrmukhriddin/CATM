package ru.metasharks.catm.step.types.dateandtime

import android.view.View
import androidx.core.widget.addTextChangedListener
import ru.metasharks.catm.feature.step.databinding.StepPatternItemDateAndTimeBinding
import ru.metasharks.catm.step.StepManager
import ru.metasharks.catm.step.utils.StepErrorUtils
import ru.metasharks.catm.step.validator.StatusListener
import ru.metasharks.catm.step.validator.Validator
import ru.metasharks.catm.utils.date.LocalDateUtils

class DateAndTimeManager :
    StepManager<StepPatternItemDateAndTimeBinding, String, Pair<String, String>>(
        { StepPatternItemDateAndTimeBinding.bind(it) }
    ) {

    override fun gatherDataFromView(view: View): String? {
        val binding = binder(view)
        if (binding.inputDate.text.isNullOrEmpty() || binding.inputTime.text.isNullOrEmpty()) {
            return null
        }
        return getFinalString(binding.inputDate.text.toString(), binding.inputTime.text.toString())
    }

    override fun gatherRestoreDataFromView(view: View): Pair<String, String>? {
        val binding = binder(view)
        val dateString = binding.inputDate.text.toString()
        val timeString = binding.inputTime.text.toString()
        return if (checkIfFilled(dateString, timeString)) {
            dateString to timeString
        } else {
            null
        }
    }

    override fun clear(view: View) {
        val binding = binder(view)
        binding.inputTime.text.clear()
        binding.inputDate.text.clear()
    }

    override fun signToStatusListener(view: View, tag: String, statusListener: StatusListener) {
        val binding = binder(view)
        binding.inputTime.addTextChangedListener {
            statusListener.update(tag, checkIfFilled(binding))
        }
        binding.inputDate.addTextChangedListener {
            statusListener.update(tag, checkIfFilled(binding))
        }
        statusListener.update(tag, checkIfFilled(binding))
    }

    override fun showError(view: View, error: Validator.Result) {
        val binding = binder(view)
        StepErrorUtils.showError(error.warning, binding.root, binding.inputDate, binding.inputTime)
    }

    private fun checkIfFilled(binding: StepPatternItemDateAndTimeBinding): Boolean {
        val inputDate = binding.inputDate.text.toString()
        val inputTime = binding.inputTime.text.toString()
        return checkIfFilled(inputDate, inputTime)
    }

    private fun getFinalString(dateString: String, timeString: String): String? {
        if (checkIfFilled(dateString, timeString)) {
            return LocalDateUtils.squashDateAndTimeToISO8601(dateString, timeString)
        }
        return null
    }

    private fun checkIfFilled(dateString: String, timeString: String): Boolean {
        return dateRegex.matches(dateString) && timeRegex.matches(timeString)
    }

    companion object {
        val timeRegex = Regex("\\d\\d:\\d\\d")
        val dateRegex = Regex("\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d")
    }
}
