package ru.metasharks.catm.feature.workpermit.ui.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class WorkerUi(
    val workerId: Long,
    val user: UserListItemUi,
    val signed: Boolean?,
    val signedByInstructor: Boolean?,
    val replacementTo: Long?,
    val isReplaced: Boolean,
) : BaseListItem {

    override val id: String = user.userId.toString()
}
