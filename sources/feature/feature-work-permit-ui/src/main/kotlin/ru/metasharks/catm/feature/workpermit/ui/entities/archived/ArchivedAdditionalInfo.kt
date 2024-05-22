package ru.metasharks.catm.feature.workpermit.ui.entities.archived

import ru.metasharks.catm.feature.workpermit.ui.entities.AdditionalInfo
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitUi
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.GasAirAnalysisUi

data class ArchivedAdditionalInfo(
    val dailyPermitsList: List<DailyPermitUi>,
    val gasAnalysisList: List<GasAirAnalysisUi>,
) : AdditionalInfo
