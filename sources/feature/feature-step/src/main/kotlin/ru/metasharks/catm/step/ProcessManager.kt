package ru.metasharks.catm.step

import android.content.Context
import android.text.SpannableStringBuilder
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.metasharks.catm.feature.step.R
import ru.metasharks.catm.step.entities.BaseStep
import ru.metasharks.catm.step.entities.ProcessEntity
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.utils.getString

abstract class ProcessManager(
    private val fragmentManager: FragmentManager,
    private val fragmentContainer: FrameLayout,
    private val stepDescriptionTextView: TextView,
    private val onEndCallback: () -> Unit
) {

    private val context: Context
        get() = fragmentContainer.context

    private lateinit var process: ProcessEntity

    fun init(process: ProcessEntity) {
        this.process = process
    }

    fun start() {
        process.init()
        showStep(process.currentStep)
    }

    abstract fun fragmentFactory(tag: String, restoreData: RestoreData?): Fragment

    private fun showStep(currentStep: BaseStep) {
        val tag = currentStep.tag
        val fragment = fragmentFactory(tag, currentStep.restoreData)

        val transaction = fragmentManager.beginTransaction()

        val currentFragment = fragmentManager.fragments.find { it.isVisible }

        currentFragment?.let { transaction.remove(it) }

        transaction.add(fragmentContainer.id, fragment, tag)
        transaction.commit()
        setStepDescription(currentStep)
    }

    private fun setStepDescription(currentStep: BaseStep) {
        if (currentStep.number < 1) {
            stepDescriptionTextView.text = context.getString(currentStep.description)
        } else {
            val context = stepDescriptionTextView.context
            val spannableStringBuilder = SpannableStringBuilder()
            val color = ContextCompat.getColor(context, ru.metasharks.catm.core.ui.R.color.blue)
            val stepNumber = context.getString(R.string.step, currentStep.number)
            spannableStringBuilder
                .color(color) { append(stepNumber) }
                .append(" ${context.getString(currentStep.description)}")
            stepDescriptionTextView.text = spannableStringBuilder
        }
    }

    fun back() {
        if (process.back()) {
            showStep(process.currentStep)
        }
    }

    fun nextStep() {
        if (process.proceed()) {
            showStep(process.currentStep)
        } else {
            end()
        }
    }

    private fun end() {
        onEndCallback()
    }
}
