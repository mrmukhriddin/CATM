package ru.metasharks.catm.feature.workpermit.entities.create

import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkPermitInfo(
    val organization: String,
    val place: String,
    val startTime: String,
    val expirationTime: String,
    val shift: String,
    val responsibleManagerId: Long,
    val responsibleExecutorId: Long,
    val permitIssuerId: Long,
    val permitAcceptorId: Long,
    val workTypeId: Int,
    val chosenDangerousFactors: List<Long>,
    val chosenAnotherFactors: List<Long>,
    val chosenSaveEquipment: List<Long>,
    val chosenUsedEquipment: List<Long>,
    val chosenWorkScheme: List<Long>,
    val workersIds: List<Long>,
    val fileUriString: String,
    val approvalResponsibleManagerId: Long,
    val admittingPersonId: Long,
    val industrySafetyOfficerId: Long,
    val workSafetyOfficerId: Long,
    val approverId: Long,
)
