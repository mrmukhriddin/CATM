package ru.metasharks.catm.feature.workpermit.ui.entities.signed

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class GasAirAnalysisUi(
    val analysisId: Long,
    val date: LocalDate,
    val isCreator: Boolean,
    val probeTime: LocalTime,
    val probePlace: String,
    val probeComponents: String,
    val probePermissibleConcentration: String,
    val probeConcentration: String,
    val probeDeviceModel: String,
    val probeDeviceNumber: String,
    val probeNext: LocalDate,
    val probeResponsiblePerson: String,
) : BaseListItem {

    val probeDevice: String = probeDeviceModel.plus(' ').plus(probeDeviceNumber)

    override val id: String
        get() = analysisId.toString()
}
