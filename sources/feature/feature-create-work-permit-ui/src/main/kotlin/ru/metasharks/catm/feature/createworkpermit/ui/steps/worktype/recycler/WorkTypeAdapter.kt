package ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

internal class WorkTypeAdapter(
    onItemClickListener: OnNumberedItemClickListener
) : PaginationListDelegationAdapter(null) {

    init {
        delegatesManager.addDelegate(NumberedItemDelegate(onItemClickListener))
    }
}
