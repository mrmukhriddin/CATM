package ru.metasharks.catm.feature.workpermit.ui.entities

import ru.metasharks.catm.feature.workpermit.entities.SignerRole
import ru.metasharks.catm.feature.workpermit.entities.StatusCode

internal data class WorkPermitDetailsUi(
    val mainInfo: WorkPermitInnerUi,
    val status: StatusCode,
    val document: DocumentUi?,
    val workersCount: Int,
    val workersSignedCount: Int,
    val signersCount: Int,
    val signersSignedCount: Int,
    val additionalDocumentsCount: Int,
    val workers: List<WorkerUi>,
    val signers: List<SignerListItemUi>,
    val additionalDocuments: List<DocumentUi>,
    val isCreator: Boolean,
    val isPermitter: Boolean,
    val additionalInfo: AdditionalInfo? = null,
    val offlinePendingActionsAreOut: Boolean? = null,
) {

    fun anyOfflineActionEnabled(): Boolean {
        return offlinePendingActionsAreOut != true
    }

    fun isRoleSigned(signerRole: SignerRole, signStatus: Boolean?): Boolean {
        return signers.first { it.role == signerRole.code }.signed == signStatus
    }
}
