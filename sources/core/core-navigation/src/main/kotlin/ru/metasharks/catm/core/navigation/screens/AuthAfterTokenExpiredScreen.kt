package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface AuthAfterTokenExpiredScreen {

    operator fun invoke(): Screen
}
