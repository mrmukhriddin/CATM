package ru.metasharks.catm.feature.workpermit.ui.mapper.request

import ru.metasharks.catm.feature.workpermit.entities.certain.workers.BrigadeX
import ru.metasharks.catm.feature.workpermit.entities.certain.workers.UpdateWorkersRequestX
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import javax.inject.Inject

class UpdateWorkersMapper @Inject constructor() {

    fun mapToRequest(items: List<WorkerListItemUi>): UpdateWorkersRequestX {
        return UpdateWorkersRequestX(
            brigade = items.map { item ->
                BrigadeX(
                    item.userId,
                    item.replacingWorkerId,
                )
            }
        )
    }
}
