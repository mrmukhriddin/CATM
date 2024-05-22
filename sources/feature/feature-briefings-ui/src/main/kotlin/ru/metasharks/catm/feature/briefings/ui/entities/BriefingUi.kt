package ru.metasharks.catm.feature.briefings.ui.entities

import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.briefings.ui.ChipFactory

data class BriefingUi(
    val text: String,
    val brId: Int,
    val categoryId: Int,
    val typeId: Int,
    val statusChip: ChipItem?,
    val chipType: ChipFactory.Type?
) : BaseListItem {

    override val id: String
        get() = brId.toString()
}
