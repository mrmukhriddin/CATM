package ru.metasharks.catm.feature.profile.ui.workers.recycler

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class WorkerUI(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val position: String?,
    val avatar: String?,
) : BaseListItem {

    override val id: String
        get() = userId.toString()
}
