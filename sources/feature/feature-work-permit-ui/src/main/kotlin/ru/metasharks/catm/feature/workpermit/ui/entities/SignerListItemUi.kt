package ru.metasharks.catm.feature.workpermit.ui.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class SignerListItemUi(
    val user: UserListItemUi,
    val role: String,
    val roleName: String,
    val signed: Boolean?
) : BaseListItem {

    override val id: String = user.id
}
