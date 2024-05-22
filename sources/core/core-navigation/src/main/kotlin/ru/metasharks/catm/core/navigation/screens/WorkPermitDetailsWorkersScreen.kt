package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface WorkPermitDetailsWorkersScreen {

    operator fun invoke(workPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.work_permit_details_workers_screen"
    }
}
