package ru.metasharks.catm.feature.profile.ui.profile.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class ListSignedData(
    val title: String,
    val signed: Boolean,
) : BaseListItem {

    override val id: String
        get() = title
}
