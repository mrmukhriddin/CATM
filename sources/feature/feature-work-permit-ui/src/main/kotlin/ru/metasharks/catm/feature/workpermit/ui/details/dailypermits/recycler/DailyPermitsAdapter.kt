package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

class DailyPermitsAdapter(
    onDeleteClick: OnDailyPermitAction,
    onEndAndSignClick: OnDailyPermitAction,
    onSignClick: OnDailyPermitAction,
) : PaginationListDelegationAdapter(null) {

    init {
        delegatesManager.addDelegate(
            DailyPermitDelegate(
                onSignClick = onSignClick,
                onDeleteClick = onDeleteClick,
                onEndAndSignClick = onEndAndSignClick,
            )
        ).addDelegate(DailyPermitPendingDelegate())
    }
}
