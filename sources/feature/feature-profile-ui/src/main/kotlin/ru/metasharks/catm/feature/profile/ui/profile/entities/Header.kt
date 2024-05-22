package ru.metasharks.catm.feature.profile.ui.profile.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class Header(
    val header: String,
) : BaseListItem {

    override val id: String
        get() = header
}
