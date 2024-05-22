package ru.metasharks.catm.feature.workpermit.ui.mapper

import ru.metasharks.catm.feature.workpermit.entities.StatusCode
import ru.metasharks.catm.feature.workpermit.ui.details.workers.WorkersViewModel
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkPermitDetailsUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.SignedAdditionalInfo
import ru.metasharks.catm.utils.FeatureToggle
import javax.inject.Inject

internal class ButtonStateMapper @Inject constructor() {

    @Suppress("ReturnCount")
    fun getButtonState(
        workPermit: WorkPermitDetailsUi,
        inOfflineMode: Boolean = false
    ): WorkersViewModel.ButtonState {
        if (workPermit.status != StatusCode.SIGNED) {
            return WorkersViewModel.ButtonState.Hidden
        }
        val additionalInfo = workPermit.additionalInfo as SignedAdditionalInfo
        if (additionalInfo.offlinePendingAddWorkersAction) {
            return WorkersViewModel.ButtonState.PendingAction(isPendingAdd = true)
        } else if (additionalInfo.offlinePendingSignWorkersAction) {
            return WorkersViewModel.ButtonState.PendingAction(isPendingSign = true)
        }
        val workers = workPermit.workers
        val workersNotSigned = workers.filter { it.signedByInstructor != true }
        return if (workersNotSigned.isNotEmpty()) {
            val isAvailable = inOfflineMode.not() && workersNotSigned.all { it.signed == true } ||
                    inOfflineMode && FeatureToggle.SIGN_NEW_ADDED_WORKERS.isEnabled
            WorkersViewModel.ButtonState.SignNewWorkers(
                isAvailable = isAvailable,
                briefingId = workPermit.mainInfo.briefingId
            )
        } else {
            WorkersViewModel.ButtonState.Hidden
        }
    }
}
