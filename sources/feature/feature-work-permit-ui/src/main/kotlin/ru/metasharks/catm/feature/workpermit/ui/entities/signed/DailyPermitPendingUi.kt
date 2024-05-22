package ru.metasharks.catm.feature.workpermit.ui.entities.signed

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class DailyPermitPendingUi(
    val permitterSignerName: String,
) : BaseListItem {

    override val id: String
        get() = "DailyPermitPendingUi_$permitterSignerName"
}
