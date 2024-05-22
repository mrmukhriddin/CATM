package ru.metasharks.catm.feature.workpermit.ui.mapper

import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemPendingUi
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import javax.inject.Inject

internal class OfflineWorkersMapper @Inject constructor() {

    fun mapFromUiToOffline(worker: WorkerListItemUi): PendingActionPayload.AddNewWorkers.Worker {
        return PendingActionPayload.AddNewWorkers.Worker(
            userId = worker.userId,
            name = worker.name,
            surname = worker.surname,
            isReady = worker.isReady,
            avatar = worker.avatar,
            position = worker.position
        )
    }

    fun mapFromOfflineToPendingUi(worker: PendingActionPayload.AddNewWorkers.Worker): WorkerListItemPendingUi {
        return WorkerListItemPendingUi(
            userId = worker.userId,
            name = worker.name,
            surname = worker.surname,
            isReady = worker.isReady,
            avatar = worker.avatar,
            position = worker.position
        )
    }
}
