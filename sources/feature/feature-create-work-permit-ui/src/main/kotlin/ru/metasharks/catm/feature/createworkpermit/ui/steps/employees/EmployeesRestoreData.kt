package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler.EmployeeUi
import ru.metasharks.catm.step.entities.RestoreData

@Parcelize
class EmployeesRestoreData(
    val workerItems: List<Item>
) : RestoreData() {

    @Parcelize
    class Item(
        val avatarUrl: String?,
        val position: String?,
        val name: String,
        val surname: String,
        val id: Long,
        val isReady: Boolean,
    ) : Parcelable

    companion object {

        fun workerItemsToRestore(workerItems: List<EmployeeUi>): List<Item> {
            return workerItems.map {
                Item(
                    avatarUrl = it.avatar,
                    name = it.name,
                    surname = it.surname,
                    id = it.workerId,
                    isReady = it.isReady,
                    position = it.position
                )
            }
        }

        fun restoreToWorkerItems(items: List<Item>): List<EmployeeUi> {
            return items.map {
                EmployeeUi(
                    workerId = it.id,
                    avatar = it.avatarUrl,
                    name = it.name,
                    surname = it.surname,
                    isReady = it.isReady,
                    position = it.position,
                    isSelected = true
                )
            }
        }
    }
}
