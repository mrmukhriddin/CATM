package ru.metasharks.catm.step.utils

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.metasharks.catm.core.ui.utils.changeBackgroundStrokeColor
import ru.metasharks.catm.core.ui.view.LabeledWrapper
import ru.metasharks.catm.feature.step.databinding.StepErrorBinding
import ru.metasharks.catm.utils.layoutInflater

object StepErrorUtils {

    fun showError(errorText: String?, container: LabeledWrapper, vararg inputs: View) {
        val errorView = container.findViewWithTag<TextView>(TAG_ERROR)
        if (errorText != null && errorView == null) {
            container.addView(inflateErrorView(container, errorText))
        }
        for (each in inputs) {
            if (each.isEnabled.not()) {
                continue
            }
            each.changeBackgroundStrokeColor(ru.metasharks.catm.core.ui.R.color.error_red)
            each.setOnTouchListener { v, event ->
                if (v.isFocused) {
                    hideError(container, inputs)
                }
                false
            }
            each.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    hideError(container, inputs)
                }
            }
        }
    }

    private fun hideError(container: LabeledWrapper, inputs: Array<out View>) {
        val errorView = container.findViewWithTag<TextView>(TAG_ERROR)
        errorView?.let { container.removeView(it) }
        for (each in inputs) {
            if (each.isEnabled.not()) {
                continue
            }
            each.changeBackgroundStrokeColor(ru.metasharks.catm.core.ui.R.color.stroke_gray)
            each.onFocusChangeListener = null
            each.setOnTouchListener(null)
        }
    }

    private fun inflateErrorView(parent: ViewGroup, errorText: String): TextView {
        val binding = StepErrorBinding.inflate(parent.context.layoutInflater, parent, false)
        binding.root.text = errorText
        binding.root.tag = TAG_ERROR
        return binding.root
    }

    private const val TAG_ERROR = "error_view"
}
