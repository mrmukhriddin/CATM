package ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.recycler

import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi

class WorkerAdapter(
    onAddActionButtonClick: (WorkerListItemUi) -> Unit,
    onChangeActionButtonClick: (WorkerListItemUi) -> Unit,
    onNearTheEndListener: OnNearTheEndListener? = null,
    private val changeMode: Boolean
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        delegatesManager.addDelegate(
            WorkerDelegate(
                onAddActionButtonClick,
                onChangeActionButtonClick,
                isChangeModeGetter = { changeMode }
            )
        ).addDelegate(EmptySearchResultDelegate)
    }

    fun notifyItemChanged(item: SelectableBaseListItem) {
        val index = items?.indexOfFirst { it.id == item.id } ?: return
        notifyItemChanged(index, Payload(PAYLOAD_SELECT, item.isSelected))
    }

    fun removeItem(workerUi: WorkerListItemUi) {
        val newItems = items?.toList().orEmpty()
        val indexOfRemovedItem = newItems.indexOfFirst { it.id == workerUi.id }
        items = newItems.filterIndexed { index, _ -> index != indexOfRemovedItem }
        notifyItemRemoved(indexOfRemovedItem)
    }

    fun getWorkers(): List<WorkerListItemUi> {
        return items?.filterIsInstance<WorkerListItemUi>().orEmpty()
    }

    fun getSelectedWorkers(): List<WorkerListItemUi> {
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
