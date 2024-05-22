package ru.metasharks.catm.core.ui.dialog.pick

import ru.metasharks.catm.core.ui.recycler.SelectableBaseListItem
import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class PickItemAdapter(
    onItemClick: (PickItemDialog.ItemUi) -> Unit,
    onNearTheEndListener: OnNearTheEndListener,
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        delegatesManager
            .addDelegate(PickItemDelegate(onItemClick))
            .addDelegate(EmptySearchResultDelegate)
    }

    fun selectItem(itemUi: SelectableBaseListItem) {
        val oldList = items ?: emptyList()
        val indexOfItem = oldList.indexOfFirst { it.areSame(itemUi) }
        notifyItemChanged(indexOfItem, itemUi.isSelected)
    }
}
