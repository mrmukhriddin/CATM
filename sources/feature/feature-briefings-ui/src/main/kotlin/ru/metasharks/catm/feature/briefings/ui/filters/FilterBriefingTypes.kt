package ru.metasharks.catm.feature.briefings.ui.filters

import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi
import javax.inject.Inject

class FilterBriefingTypes @Inject constructor() {

    /**
     * Фильтруем сначала инструктажи по categoryId
     * Из них выцепляем уникальные briefingTypes
     * Возвращаем их, для того, чтобы отобразить в адаптере
     */
    operator fun invoke(
        briefings: List<BriefingUi>,
        types: List<BriefingTypeUi>,
        categoryId: Int
    ): List<BriefingTypeUi> {
        return briefings.filter { it.categoryId == categoryId }
            .let { briefs ->
                val briefTypeIds = briefs.map { it.typeId }.distinct()
                types.filter { type -> type.typeId in briefTypeIds }
            }
    }
}
