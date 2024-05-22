package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem

@Parcelize
data class EmployeeUi(
    val workerId: Long,
    val surname: String,
    val name: String,
    val isReady: Boolean,
    val position: String?,
    val avatar: String?,
    override var isSelected: Boolean = false,
) : SelectableBaseListItem, Parcelable {

    val fullName: String
        get() = "$name $surname"

    override val id: String
        get() = workerId.toString()
}
