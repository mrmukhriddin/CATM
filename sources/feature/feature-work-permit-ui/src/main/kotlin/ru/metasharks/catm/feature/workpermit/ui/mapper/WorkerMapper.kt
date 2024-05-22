package ru.metasharks.catm.feature.workpermit.ui.mapper

import ru.metasharks.catm.feature.workpermit.entities.certain.WorkerX
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.feature.workpermit.ui.entities.UserListItemUi
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkerUi
import javax.inject.Inject

class WorkerMapper @Inject constructor() {

    fun mapWorker(item: WorkerX): WorkerUi {
        return WorkerUi(
            workerId = item.id,
            user = UserListItemUi(
                userId = item.user.id,
                name = item.user.firstName,
                middleName = item.user.middleName,
                surname = item.user.lastName,
                avatar = item.user.avatar,
                isReady = item.signed,
                position = item.user.position
            ),
            signed = item.signed,
            signedByInstructor = item.signedByInstructor,
            replacementTo = item.replacementTo,
            isReplaced = item.replacementTo != null,
        )
    }

    fun mapWorkerToListEntity(item: WorkPermitUserX): WorkerListItemUi {
        return WorkerListItemUi(
            userId = item.id,
            isReady = item.isReady,
            name = item.firstName,
            surname = item.lastName,
            position = item.position,
            avatar = item.avatar,
        )
    }
}
