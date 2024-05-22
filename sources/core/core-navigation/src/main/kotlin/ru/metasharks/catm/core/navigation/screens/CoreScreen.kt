package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface CoreScreen {

    operator fun invoke(): Screen
}
