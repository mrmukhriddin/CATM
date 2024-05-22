package ru.metasharks.catm.feature.workpermit.ui.list

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class StatusUiItem(
    val statusCode: String,
    val statusLocalizedName: String,
) : BaseListItem {

    override val id: String
        get() = statusCode
}
