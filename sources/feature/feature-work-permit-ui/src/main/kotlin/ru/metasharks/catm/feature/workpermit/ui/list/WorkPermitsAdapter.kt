package ru.metasharks.catm.feature.workpermit.ui.list

import ru.metasharks.catm.core.ui.recycler.empty.response.EmptyResponseDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class WorkPermitsAdapter(
    onNearTheEndListener: OnNearTheEndListener,
    onWorkPermitClick: OnWorkPermitClick
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        delegatesManager.addDelegate(EmptyResponseDelegate)
        delegatesManager.addDelegate(WorkPermitsDelegate(onWorkPermitClick))
        delegatesManager.addDelegate(WorkPermitsPendingDelegate())
    }
}
