package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.androidx.ActivityScreen

fun interface ProfileScreen {

    operator fun invoke(userId: Int): ActivityScreen
}
