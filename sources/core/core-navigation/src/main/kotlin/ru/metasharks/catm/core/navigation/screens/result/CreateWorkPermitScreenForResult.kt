package ru.metasharks.catm.core.navigation.screens.result

fun interface CreateWorkPermitScreenForResult {

    operator fun invoke(): ResultScreen

    companion object {

        const val KEY = "key.create_work_permits"
    }
}
