package ru.metasharks.catm.feature.briefings.ui.main

import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi

internal interface BriefingMainCallbacks {

    fun getMainViewModel(): BriefingMainViewModel

    // level 2
    fun openBriefingTypes(categoryId: Int)

    // level 3
    fun openBriefingsList(categoryId: Int, typeId: Int)

    // level 4
    fun openBriefingDetails(briefing: BriefingUi)
}
