package ru.metasharks.catm.feature.briefings.ui.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

// level 5
fun interface BriefingQuizScreen {

    operator fun invoke(briefingId: Int): ResultScreen

    companion object {

        const val KEY = "key.briefing_quiz"
    }
}
