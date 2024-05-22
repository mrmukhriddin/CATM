package ru.metasharks.catm.feature.workpermit.ui.details.gasairanalysis.recycler

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class GasAirAnalysisPendingUi(
    val probePlace: String,
    val probeComponents: String,
    val probePermissibleConcentration: String,
    val probeConcentration: String,
    val probeDeviceModel: String,
    val probeDeviceNumber: String,
) : BaseListItem {

    val probeDevice: String = probeDeviceModel.plus(' ').plus(probeDeviceNumber)

    override val id: String
        get() = "PendingGasAirAnalysis"
}
