package ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo

data class GeneralInfoStepOutput(
    val organization: String,
    val place: String,
    val startTime: String,
    val expirationTime: String,
    val shift: String,
    val responsibleManagerId: Long,
    val responsibleExecutorId: Long,
    val permitIssuerId: Long,
    val permitAcceptorId: Long,
)
