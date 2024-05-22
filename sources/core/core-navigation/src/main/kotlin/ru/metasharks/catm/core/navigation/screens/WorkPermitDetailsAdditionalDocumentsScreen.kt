package ru.metasharks.catm.core.navigation.screens

import ru.metasharks.catm.core.navigation.screens.result.ResultScreen

fun interface WorkPermitDetailsAdditionalDocumentsScreen {

    operator fun invoke(workPermitId: Long): ResultScreen

    companion object {

        const val KEY = "key.wp_details_additional_documents"
    }
}
