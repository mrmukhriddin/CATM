package ru.metasharks.catm.feature.notifications.ui.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter

internal typealias OnDismiss = (Long) -> Unit

internal class NotificationsAdapter(
    onButtonActionClick: OnButtonActionClick,
    onDismiss: OnDismiss,
) : PaginationListDelegationAdapter(null) {

    init {
        delegatesManager
            .addDelegate(ButtonActionDelegate(onButtonActionClick, onDismiss))
            .addDelegate(InformationDelegate(onDismiss))
    }

    companion object {

        const val EXTEND_DELETE_ZONE_DP = 8
    }
}
