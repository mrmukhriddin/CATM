package ru.metasharks.catm.feature.workpermit.ui.entities.signed

import ru.metasharks.catm.feature.workpermit.ui.entities.AdditionalInfo

data class SignedAdditionalInfo(
    val dailyPermitsList: List<DailyPermitUi>,
    val gasAnalysisList: List<GasAirAnalysisUi>,
    val extension: ExtensionUi?,
    val offlinePendingClose: Boolean,
    val offlinePendingExtend: Boolean,
    val offlinePendingSignExtension: Boolean,
    val offlinePendingAddWorkersAction: Boolean,
    val offlinePendingSignWorkersAction: Boolean,
) : AdditionalInfo
