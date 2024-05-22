package ru.metasharks.catm.feature.workpermit.ui.mapper

import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.feature.workpermit.entities.worktype.WorkTypeX
import javax.inject.Inject

class WorkTypeMapper @Inject constructor() {

    fun mapWorkTypeToPickItem(workType: WorkTypeX): PickItemDialog.ItemUi {
        return PickItemDialog.ItemUi(
            entityId = workType.id,
            value = workType.title
        )
    }
}
