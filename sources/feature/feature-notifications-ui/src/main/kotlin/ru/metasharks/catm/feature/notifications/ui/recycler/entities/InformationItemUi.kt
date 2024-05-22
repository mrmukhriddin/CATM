package ru.metasharks.catm.feature.notifications.ui.recycler.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

internal data class InformationItemUi(
    val notificationId: Long,
    val message: String,
    val createdDate: String,
) : BaseListItem {

    override val id: String
        get() = notificationId.toString()
}
