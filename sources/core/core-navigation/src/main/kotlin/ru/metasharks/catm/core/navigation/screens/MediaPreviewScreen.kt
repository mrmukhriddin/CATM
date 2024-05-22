package ru.metasharks.catm.core.navigation.screens

import com.github.terrakok.cicerone.Screen

fun interface MediaPreviewScreen {

    operator fun invoke(fileUri: String, title: String?): Screen
}
