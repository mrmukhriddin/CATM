package ru.metasharks.catm.core.navigation.screens.fragments

import com.github.terrakok.cicerone.androidx.FragmentScreen

fun interface PlaceholderScreen {

    operator fun invoke(): FragmentScreen
}
