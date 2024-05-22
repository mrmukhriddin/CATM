package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface EndDailyPermitScreen {

    operator fun invoke(workPermitId: Long, dailyPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.end_daily_permit_screen"
    }
}
