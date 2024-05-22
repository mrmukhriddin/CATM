package ru.metasharks.catm.feature.briefings.ui.mappers

import ru.metasharks.catm.feature.briefings.entities.BriefingCategoryX
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingCategoryUi
import javax.inject.Inject

internal class BriefingCategoryMapper @Inject constructor() {

    fun map(items: List<BriefingCategoryX>): List<BriefingCategoryUi> {
        return items.map { item ->
            BriefingCategoryUi(
                text = item.value,
                categoryId = item.id
            )
        }
    }
}
