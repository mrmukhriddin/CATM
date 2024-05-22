package ru.metasharks.catm.feature.briefings.ui.screens

import com.github.terrakok.cicerone.androidx.FragmentScreen

// level 3
internal fun interface BriefingsListScreen {

    operator fun invoke(categoryId: Int, typeId: Int): FragmentScreen
}
