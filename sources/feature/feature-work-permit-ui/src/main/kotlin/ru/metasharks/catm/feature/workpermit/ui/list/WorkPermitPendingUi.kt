package ru.metasharks.catm.feature.workpermit.ui.list

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class WorkPermitPendingUi(
    val address: String,
    val workersCount: Int,
) : BaseListItem {

    override val id: String
        get() = "WorkPermitPendingUiItem"
}
