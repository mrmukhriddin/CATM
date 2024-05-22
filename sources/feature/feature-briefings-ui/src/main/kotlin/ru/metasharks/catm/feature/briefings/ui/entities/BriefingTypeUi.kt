package ru.metasharks.catm.feature.briefings.ui.entities

import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.briefings.ui.ChipFactory

data class BriefingTypeUi(
    val text: String,
    val typeId: Int,
    val chipItem: ChipItem?,
    val chipType: ChipFactory.Type?,
) : BaseListItem {

    override val id: String
        get() = typeId.toString()
}
