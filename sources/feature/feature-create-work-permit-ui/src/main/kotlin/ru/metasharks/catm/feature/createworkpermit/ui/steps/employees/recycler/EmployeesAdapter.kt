package ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.recycler

import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class EmployeesAdapter(
    onAddActionButtonClick: (EmployeeUi) -> Unit,
    onNearTheEndListener: OnNearTheEndListener? = null
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        delegatesManager.addDelegate(EmployeeDelegate(onAddActionButtonClick))
            .addDelegate(EmptySearchResultDelegate)
    }

    fun notifyItemChanged(item: SelectableBaseListItem) {
        val index = items?.indexOfFirst { it.id == item.id } ?: return
        notifyItemChanged(index, Payload(PAYLOAD_SELECT, item.isSelected))
    }

    fun removeItem(workerUi: EmployeeUi) {
        val newItems = items?.toList() ?: emptyList()
        val indexOfRemovedItem = newItems.indexOfFirst { it.id == workerUi.id }
        items = newItems.filterIndexed { index, _ -> index != indexOfRemovedItem }
        notifyItemRemoved(indexOfRemovedItem)
    }

    fun getWorkers(): List<EmployeeUi> {
        return items?.filterIsInstance<EmployeeUi>() ?: emptyList()
    }

    fun getSelectedWorkers(): List<EmployeeUi> {
        return getWorkers()
    }

    class Payload(
        val code: String,
        val isSelected: Boolean
    )

    companion object {

        const val PAYLOAD_SELECT = "select"
    }
}
