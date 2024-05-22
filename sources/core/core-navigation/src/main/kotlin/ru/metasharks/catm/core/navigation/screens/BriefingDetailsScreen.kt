package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

// level 4
fun interface BriefingDetailsScreen {

    operator fun invoke(briefingId: Int): ResultScreen

    companion object {

        const val KEY = "key.briefing_details_screen"
    }
}
