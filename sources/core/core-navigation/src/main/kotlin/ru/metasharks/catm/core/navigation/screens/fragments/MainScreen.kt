package ru.metasharks.catm.core.navigation.screens.fragments

import com.github.terrakok.cicerone.androidx.FragmentScreen

fun interface MainScreen {

    operator fun invoke(): FragmentScreen
}
