package ru.metasharks.catm.feature.workpermit.ui.mapper

import android.content.Context
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.list.StatusUiItem
import javax.inject.Inject

class StatusesMapper @Inject constructor(
    private val context: Context,
) {

    fun mapStatuses(statuses: List<StatusX>): List<StatusUiItem> {
        return listOf(StatusUiItem(StatusX.ALL, context.getString(R.string.wp_status_all)))
            .plus(statuses.map(this::mapStatus))
    }

    private fun mapStatus(status: StatusX): StatusUiItem {
        return StatusUiItem(
            statusLocalizedName = status.statusName,
            statusCode = status.status,
        )
    }
}
