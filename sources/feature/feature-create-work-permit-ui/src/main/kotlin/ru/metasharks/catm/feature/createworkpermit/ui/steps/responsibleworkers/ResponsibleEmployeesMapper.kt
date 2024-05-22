package ru.metasharks.catm.feature.createworkpermit.ui.steps.responsibleworkers

import ru.metasharks.catm.core.ui.dialog.pick.PickItemDialog
import ru.metasharks.catm.core.ui.utils.getShortFormOfFullName
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX
import javax.inject.Inject

class ResponsibleEmployeesMapper @Inject constructor() {

    fun mapResponsibleManagerToPickItem(item: WorkPermitUserX): PickItemDialog.ItemUi {
        return PickItemDialog.ItemUi(
            entityId = item.id,
            value = getShortFormOfFullName(item.lastName, item.firstName, item.middleName)
        )
    }
}
