package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem

@Parcelize
data class WorkerListItemUi(
    val userId: Long,
    val name: String,
    val surname: String,
    val isReady: Boolean,
    val avatar: String?,
    val position: String?,
    val replacingWorkerId: Long? = null,
    override var isSelected: Boolean = false,
) : SelectableBaseListItem, Parcelable {

    val fullName: String
        get() = "$name $surname"

    override val id: String
        get() = userId.toString()
}
