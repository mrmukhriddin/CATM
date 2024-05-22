package ru.metasharks.catm.feature.workpermit.ui.entities

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem

class WorkPermitInnerUi(
    val permitId: Long,
    val createdDate: LocalDate,
    val startDate: LocalDate,
    val startTime: LocalTime,
    val endDate: LocalDate,
    val endTime: LocalTime,
    val chips: List<ChipItem>,
    val workTypeChip: ChipItem,
    val address: String,
    val workersCount: Int,
    val briefingId: Long,
) : BaseListItem {

    override val id: String
        get() = "WorkPermitUiItem $permitId"
}
