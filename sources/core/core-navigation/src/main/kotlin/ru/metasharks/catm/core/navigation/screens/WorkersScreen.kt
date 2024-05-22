package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface WorkersScreen {

    operator fun invoke(): Screen
}
