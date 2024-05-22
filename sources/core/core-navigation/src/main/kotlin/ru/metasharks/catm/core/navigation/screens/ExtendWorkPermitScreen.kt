package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface ExtendWorkPermitScreen {

    operator fun invoke(workPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.extend_work_permit"
    }
}
