package ru.metasharks.catm.feature.briefings.ui.filters

import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import javax.inject.Inject

class FilterBriefingItems @Inject constructor() {

    /**
     * Фильтруем инструктажи по categoryId & typeId
     */
    operator fun invoke(
        briefings: List<BriefingUi>,
        categoryId: Int,
        typeId: Int,
    ): List<BriefingUi> {
        return briefings.filter { it.categoryId == categoryId && it.typeId == typeId }
    }
}
