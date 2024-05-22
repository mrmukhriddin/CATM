package ru.metasharks.catm.step.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class ProcessEntity(
    private val steps: MutableList<BaseStep> = mutableListOf(),
) {

    val currentProgress: Progress
        get() {
            return when {
                currentStep.number < 1 -> Progress.PREPARATION
                currentStepIndex - steps.count { it.number < 1 } == 0 && currentStep.number >= 1 ->
                    Progress.START
                currentStepIndex == steps.lastIndex -> Progress.END
                else -> Progress.IN_PROCESS
            }
        }

    lateinit var currentStep: BaseStep
    private var currentStepIndex = -1
        set(value) {
            field = value
            currentStep = steps[field]
        }

    fun init() {
        currentStepIndex = 0
    }

    fun addSteps(vararg steps: BaseStep) {
        steps.sortBy { it.number }
        this.steps.addAll(steps)
    }

    fun addStep(step: BaseStep) {
        steps.add(step)
    }

    fun back(): Boolean {
        if (currentStepIndex - 1 < 0) {
            return false
        }
        currentStepIndex--
        return true
    }

    fun proceed(): Boolean {
        if (currentStepIndex + 1 >= steps.size) {
            return false // it is the last step
        }
        currentStepIndex++
        return true
    }
}

enum class Progress {
    PREPARATION, START, IN_PROCESS, END
}

abstract class RestoreData : Parcelable

@Parcelize
class StepPatternRestoreData(
    val mutableMap: @RawValue MutableMap<String, Any?> = mutableMapOf()
) : RestoreData() {

    fun <T> byTag(tag: String): T? {
        return mutableMap[tag] as T?
    }
}
