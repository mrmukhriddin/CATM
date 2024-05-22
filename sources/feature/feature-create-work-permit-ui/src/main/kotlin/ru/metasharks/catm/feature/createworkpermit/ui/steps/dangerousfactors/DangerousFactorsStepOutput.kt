package ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors

data class DangerousFactorsStepOutput(
    val chosenDangerousFactors: List<Long>,
    val chosenAnotherFactors: List<Long>,
    val chosenSaveEquipment: List<Long>,
)
