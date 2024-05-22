package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface OfflineSwitcherScreen {

    operator fun invoke(fromOnline: Boolean, force: Boolean): Screen
}
