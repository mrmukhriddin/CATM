package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees

import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX
import javax.inject.Inject

class EmployeeMapper @Inject constructor() {

    fun mapEmployeesToUi(list: List<WorkPermitUserX>): List<EmployeeUi> {
        return list.map { mapEmployeeToUi(it) }
    }

    fun mapEmployeeToUi(item: WorkPermitUserX): EmployeeUi {
        return EmployeeUi(
            workerId = item.id,
            surname = item.lastName,
            name = item.firstName,
            isReady = item.isReady,
            avatar = item.avatar,
            position = item.position
        )
    }
}
