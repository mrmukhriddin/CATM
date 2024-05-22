package ru.metasharks.catm.feature.briefings.ui.screens

import com.github.terrakok.cicerone.androidx.FragmentScreen

// level 2
fun interface BriefingTypesScreen {

    operator fun invoke(categoryId: Int): FragmentScreen
}
