package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface CreateDailyPermitScreen {

    operator fun invoke(workPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.create_daily_permit_screen"
    }
}
