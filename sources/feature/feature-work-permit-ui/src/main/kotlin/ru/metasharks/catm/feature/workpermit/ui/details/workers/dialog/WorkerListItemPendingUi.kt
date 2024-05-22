package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class WorkerListItemPendingUi(
    val userId: Long,
    val name: String,
    val surname: String,
    val isReady: Boolean,
    val avatar: String?,
    val position: String?,
) : BaseListItem {

    val fullName: String
        get() = "$name $surname"

    override val id: String
        get() = userId.toString()
}
