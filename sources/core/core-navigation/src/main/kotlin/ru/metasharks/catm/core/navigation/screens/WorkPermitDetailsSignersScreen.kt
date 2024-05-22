package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface WorkPermitDetailsSignersScreen {

    operator fun invoke(workPermitId: Long): Screen
}
