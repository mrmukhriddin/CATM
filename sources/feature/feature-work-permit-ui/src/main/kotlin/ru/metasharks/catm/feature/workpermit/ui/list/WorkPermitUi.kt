package ru.metasharks.catm.feature.workpermit.ui.list

import org.joda.time.LocalDate
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.feature.workpermit.entities.StatusCode

class WorkPermitUi(
    val permitId: Long,
    val createdDate: LocalDate,
    val extendDate: LocalDate?,
    val chips: List<ChipItem>,
    val workTypeChip: ChipItem,
    val address: String,
    val workersCount: Int,
    val statusCode: StatusCode,
) : BaseListItem {

    override val id: String
        get() = "WorkPermitUiItem $permitId"
}
