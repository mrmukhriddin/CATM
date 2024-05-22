package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface AuthScreen {

    operator fun invoke(): Screen
}
