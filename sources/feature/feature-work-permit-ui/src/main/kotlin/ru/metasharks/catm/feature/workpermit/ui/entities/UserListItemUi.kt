package ru.metasharks.catm.feature.workpermit.ui.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.utils.getShortFormOfFullName

data class UserListItemUi(
    val userId: Long,
    val name: String,
    val surname: String,
    val middleName: String,
    val isReady: Boolean?,
    val position: String?,
    val avatar: String?,
) : BaseListItem {

    override val id: String = userId.toString()

    fun getFullName(): String {
        return getShortFormOfFullName(surname, name, middleName)
    }
}
