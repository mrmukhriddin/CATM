package ru.metasharks.catm.feature.briefings.ui.mappers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.ui.ChipFactory
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import javax.inject.Inject

internal class BriefingMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun map(item: BriefingX): BriefingUi {
        return BriefingUi(
            text = item.title,
            brId = item.id,
            categoryId = item.category,
            typeId = item.type,
            statusChip = getChipItem(item),
            chipType = getChipType(item),
        )
    }

    fun getChipType(item: BriefingX): ChipFactory.Type {
        return if (item.userBriefingSignedAt == null) {
            ChipFactory.Type.WAITING
        } else {
            ChipFactory.Type.PASSED
        }
    }

    fun getChipItem(item: BriefingX): ChipItem {
        val chipType = getChipType(item)
        return getChipItem(chipType)
    }

    fun getChipItem(type: ChipFactory.Type): ChipItem {
        return ChipFactory.createChip(context, type)
    }

    fun map(items: List<BriefingX>): List<BriefingUi> = items.map(::map)
}
