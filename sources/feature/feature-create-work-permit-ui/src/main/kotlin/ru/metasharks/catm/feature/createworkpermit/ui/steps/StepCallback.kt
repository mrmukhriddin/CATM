package ru.metasharks.catm.feature.createworkpermit.ui.steps

import ru.metasharks.catm.step.entities.RestoreData

interface StepCallback {

    fun endStep(output: Any?, restoreData: RestoreData?)

    fun back()
}
