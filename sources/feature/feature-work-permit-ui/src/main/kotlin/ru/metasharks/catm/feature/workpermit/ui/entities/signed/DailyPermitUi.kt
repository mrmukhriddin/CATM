package ru.metasharks.catm.feature.workpermit.ui.entities.signed

import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.workpermit.ui.entities.SignerListItemUi

data class DailyPermitUi(
    val dailyPermitId: Long,
    val dateStart: String,
    val timeStart: String,
    val dateEnd: String?,
    val timeEnd: String?,
    val responsibleSigner: SignerListItemUi,
    val permitterSigner: SignerListItemUi,
    val isCreator: Boolean,
    val isSigner: Boolean,
    val isOffline: Boolean,
    var pendingActionSent: Boolean = false,
) : BaseListItem {

    override val id: String
        get() = dailyPermitId.toString()
}
