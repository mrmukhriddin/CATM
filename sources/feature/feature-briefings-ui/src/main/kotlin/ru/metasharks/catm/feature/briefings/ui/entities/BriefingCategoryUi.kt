package ru.metasharks.catm.feature.briefings.ui.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class BriefingCategoryUi(
    val text: String,
    val categoryId: Int,
) : BaseListItem {

    override val id: String
        get() = categoryId.toString()
}
