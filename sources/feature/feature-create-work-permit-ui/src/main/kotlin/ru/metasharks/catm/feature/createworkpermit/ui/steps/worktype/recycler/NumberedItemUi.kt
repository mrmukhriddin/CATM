package ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class NumberedItemUi(
    val numberOnList: Int,
    val itemId: Int,
    val title: String,
    val description: String,
) : BaseListItem {

    override val id: String
        get() = itemId.toString()
}
