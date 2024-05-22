package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis

import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler.GasAirAnalysisPendingUi
import javax.inject.Inject

class GasAirAnalysisMapper @Inject constructor() {

    fun mapPendingToBaseItemList(pending: PendingActionPayload.CreateGasAirAnalysis): GasAirAnalysisPendingUi {
        val info = pending.createGasAirAnalysisInfo
        return GasAirAnalysisPendingUi(
            probePlace = info.place,
            probeComponents = info.components,
            probePermissibleConcentration = info.concentration,
            probeConcentration = info.result,
            probeDeviceModel = info.deviceModel,
            probeDeviceNumber = info.deviceNumber,
        )
    }
}
