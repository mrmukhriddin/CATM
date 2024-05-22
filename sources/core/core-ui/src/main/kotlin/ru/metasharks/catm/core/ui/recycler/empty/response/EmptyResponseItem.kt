package ru.metasharks.catm.core.ui.recycler.empty.response

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class EmptyResponseItem(
    override val id: String = "EmptyResponseItem",
) : BaseListItem
