package ru.metasharks.catm.feature.workpermit.ui

import android.content.Context

typealias OnMenuClick = () -> Unit

abstract class MenuEntry {

    abstract val tagsToActions: MutableMap<String, OnMenuClick>

    abstract val options: List<MenuOption>

    abstract fun addOnMenuOptionClickListener(menuOptionTag: String, action: OnMenuClick): MenuEntry

    fun onMenuOptionClick(tag: String) {
        tagsToActions[tag]?.let { it() }
    }
}

class StatusPendingMenuEntry(
    context: Context,
    val workersCount: Pair<Int, Int>,
    val signersCount: Pair<Int, Int>,
    val additionalDocumentsCount: Int,
    val briefingPassed: Boolean,
    val error: String?,
) : MenuEntry() {

    override val tagsToActions: MutableMap<String, OnMenuClick> = hashMapOf()

    override val options: List<MenuOption> =
        listOf(
            MenuOption.Count(
                MENU_OPTION_WORKERS,
                context.getString(R.string.menu_option_workers),
                workersCount.first,
                workersCount.second,
                error
            ),
            MenuOption.Count(
                MENU_OPTION_SIGNERS,
                context.getString(R.string.menu_option_signers),
                signersCount.first,
                signersCount.second,
            ),
            MenuOption.Count(
                MENU_OPTION_ADD_DOCUMENTS,
                context.getString(R.string.menu_option_additional_documents),
                additionalDocumentsCount
            ),
            MenuOption.Check(
                MENU_OPTION_FACTORS_BRIEFING,
                context.getString(R.string.menu_option_dangerous_factors),
                briefingPassed
            ),
        )

    override fun addOnMenuOptionClickListener(
        menuOptionTag: String,
        action: OnMenuClick
    ): StatusPendingMenuEntry {
        tagsToActions[menuOptionTag] = action
        return this
    }

    companion object {

        const val MENU_OPTION_WORKERS = "workers"
        const val MENU_OPTION_SIGNERS = "signers"
        const val MENU_OPTION_ADD_DOCUMENTS = "additional_documents"
        const val MENU_OPTION_FACTORS_BRIEFING = "factors_briefing"
    }
}

class StatusNewMenuEntry(
    context: Context,
    val workersCount: Pair<Int, Int>,
    val signersCount: Pair<Int, Int>,
    val additionalDocumentsCount: Int,
    val error: String?,
) : MenuEntry() {

    override val tagsToActions: MutableMap<String, OnMenuClick> = hashMapOf()

    override val options: List<MenuOption> =
        listOf(
            MenuOption.Count(
                MENU_OPTION_WORKERS,
                context.getString(R.string.menu_option_workers),
                workersCount.first,
                workersCount.second,
                error
            ),
            MenuOption.Count(
                MENU_OPTION_SIGNERS,
                context.getString(R.string.menu_option_signers),
                signersCount.first,
                signersCount.second,
            ),
            MenuOption.Count(
                MENU_OPTION_ADD_DOCUMENTS,
                context.getString(R.string.menu_option_additional_documents),
                additionalDocumentsCount
            ),
        )

    override fun addOnMenuOptionClickListener(
        menuOptionTag: String,
        action: OnMenuClick
    ): StatusNewMenuEntry {
        tagsToActions[menuOptionTag] = action
        return this
    }

    companion object {

        const val MENU_OPTION_WORKERS = "workers"
        const val MENU_OPTION_SIGNERS = "signers"
        const val MENU_OPTION_ADD_DOCUMENTS = "additional_documents"
    }
}

class StatusSignedMenuEntry(
    context: Context,
    val workersCount: Pair<Int, Int>,
    val signersCount: Pair<Int, Int>,
    val additionalDocumentsCount: Int,
    val dailyPermitsCount: Int,
    val gasAnalysisCount: Int,
    val briefingPassed: Boolean,
    val error: String?,
) : MenuEntry() {

    override val tagsToActions: MutableMap<String, OnMenuClick> = hashMapOf()

    override val options: List<MenuOption> =
        listOf(
            MenuOption.Count(
                MENU_OPTION_WORKERS,
                context.getString(R.string.menu_option_workers),
                workersCount.first,
                workersCount.second,
                error
            ),
            MenuOption.Count(
                MENU_OPTION_SIGNERS,
                context.getString(R.string.menu_option_signers),
                signersCount.first,
                signersCount.second,
            ),
            MenuOption.Count(
                MENU_OPTION_DAILY_PERMITS,
                context.getString(R.string.menu_option_daily_permits),
                dailyPermitsCount,
            ),
            MenuOption.Count(
                MENU_OPTION_GAS_ANALYSIS,
                context.getString(R.string.menu_option_gas_analysis),
                gasAnalysisCount,
            ),
            MenuOption.Count(
                MENU_OPTION_ADD_DOCUMENTS,
                context.getString(R.string.menu_option_additional_documents),
                additionalDocumentsCount
            ),
            MenuOption.Check(
                MENU_OPTION_FACTORS_BRIEFING,
                context.getString(R.string.menu_option_dangerous_factors),
                briefingPassed
            ),
        )

    override fun addOnMenuOptionClickListener(
        menuOptionTag: String,
        action: OnMenuClick
    ): StatusSignedMenuEntry {
        tagsToActions[menuOptionTag] = action
        return this
    }

    companion object {

        const val MENU_OPTION_WORKERS = "workers"
        const val MENU_OPTION_SIGNERS = "signers"
        const val MENU_OPTION_ADD_DOCUMENTS = "additional_documents"
        const val MENU_OPTION_GAS_ANALYSIS = "gas_analysis"
        const val MENU_OPTION_DAILY_PERMITS = "daily_permits"
        const val MENU_OPTION_FACTORS_BRIEFING = "factors_briefing"
    }
}

class StatusArchivedMenuEntry(
    context: Context,
    val workersCount: Pair<Int, Int>,
    val signersCount: Pair<Int, Int>,
    val additionalDocumentsCount: Int,
    val dailyPermitsCount: Int,
    val gasAnalysisCount: Int,
    val error: String?,
) : MenuEntry() {

    override val tagsToActions: MutableMap<String, OnMenuClick> = hashMapOf()

    override val options: List<MenuOption> =
        listOf(
            MenuOption.Count(
                MENU_OPTION_WORKERS,
                context.getString(R.string.menu_option_workers),
                workersCount.first,
                workersCount.second,
                error
            ),
            MenuOption.Count(
                MENU_OPTION_SIGNERS,
                context.getString(R.string.menu_option_signers),
                signersCount.first,
                signersCount.second,
            ),
            MenuOption.Count(
                MENU_OPTION_DAILY_PERMITS,
                context.getString(R.string.menu_option_daily_permits),
                dailyPermitsCount,
            ),
            MenuOption.Count(
                MENU_OPTION_GAS_ANALYSIS,
                context.getString(R.string.menu_option_gas_analysis),
                gasAnalysisCount,
            ),
            MenuOption.Count(
                MENU_OPTION_ADD_DOCUMENTS,
                context.getString(R.string.menu_option_additional_documents),
                additionalDocumentsCount
            ),
        )

    override fun addOnMenuOptionClickListener(
        menuOptionTag: String,
        action: OnMenuClick
    ): StatusArchivedMenuEntry {
        tagsToActions[menuOptionTag] = action
        return this
    }

    companion object {

        const val MENU_OPTION_WORKERS = "workers"
        const val MENU_OPTION_SIGNERS = "signers"
        const val MENU_OPTION_ADD_DOCUMENTS = "additional_documents"
        const val MENU_OPTION_GAS_ANALYSIS = "gas_analysis"
        const val MENU_OPTION_DAILY_PERMITS = "daily_permits"
    }
}
