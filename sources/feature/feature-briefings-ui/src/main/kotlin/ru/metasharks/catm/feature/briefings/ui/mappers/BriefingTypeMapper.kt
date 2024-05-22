package ru.metasharks.catm.feature.briefings.ui.mappers

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.briefings.ui.ChipFactory
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi
import javax.inject.Inject

internal class BriefingTypeMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun map(items: List<BriefingTypeX>, briefings: List<BriefingX>): List<BriefingTypeUi> {
        return items.map { item ->
            val neededBriefings = briefings.filter { it.type == item.id }
            val chipType = getChipType(neededBriefings)
            BriefingTypeUi(
                text = item.value,
                typeId = item.id,
                chipType = chipType,
                chipItem = getChipItem(chipType),
            )
        }
    }

    fun getChipType(briefings: List<BriefingX>): ChipFactory.Type? {
        val isAnyNotSigned = briefings.any { it.userBriefingSignedAt == null }
        return when {
            isAnyNotSigned -> ChipFactory.Type.WAITING
            !isAnyNotSigned -> ChipFactory.Type.PASSED
            else -> null
        }
    }

    fun getChipItem(briefings: List<BriefingX>): ChipItem? {
        val isAnyNotSigned = briefings.any { it.userBriefingSignedAt == null }
        return when {
            isAnyNotSigned -> ChipFactory.createChip(context, ChipFactory.Type.WAITING)
            !isAnyNotSigned -> ChipFactory.createChip(context, ChipFactory.Type.PASSED)
            else -> null
        }
    }

    fun getChipItem(chipType: ChipFactory.Type?): ChipItem? {
        return chipType?.let {
            ChipFactory.createChip(context, it)
        }
    }
}
