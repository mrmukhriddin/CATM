package ru.metasharks.catm.feature.profile.ui.workers.recycler

import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class WorkersAdapter(
    onWorkerClick: (workerUI: WorkerUI) -> Unit,
    onNearTheEndListener: OnNearTheEndListener,
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        items = emptyList()
        delegatesManager.addDelegate(DelegateWorker(onWorkerClick))
            .addDelegate(EmptySearchResultDelegate)
    }
}
