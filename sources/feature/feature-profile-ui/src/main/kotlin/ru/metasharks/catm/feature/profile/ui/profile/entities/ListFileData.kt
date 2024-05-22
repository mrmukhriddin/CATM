package ru.metasharks.catm.feature.profile.ui.profile.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

class ListFileData(
    val title: String,
    val fileUri: String,
) : BaseListItem {

    override val id: String
        get() = title
}
