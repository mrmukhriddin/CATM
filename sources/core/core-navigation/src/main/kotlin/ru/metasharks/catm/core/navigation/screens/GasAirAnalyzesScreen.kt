package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface GasAirAnalyzesScreen {

    operator fun invoke(workPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.gas_air_analysis"
    }
}
