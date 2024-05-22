package ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler

import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkerUi

typealias OnWorkerClicked = (WorkerMainInfoWrapper) -> Unit

class UserAdapter(
    onWorkerClicked: OnWorkerClicked,
    private val replacingWorkersUserIdsGetter: () -> List<Long>,
) : PaginationListDelegationAdapter(null) {

    private var isEnabledReplacement: Boolean = true

    init {
        delegatesManager
            .addDelegate(UserDelegate())
            .addDelegate(UserPendingDelegate())
            .addDelegate(UserAddedDelegate(onWorkerClicked))
            .addDelegate(
                UserChangeableDelegate(
                    onWorkerClicked,
                    ::ifToReplace,
                    ::isEnabledReplacement
                )
            )
    }

    private fun ifToReplace(workerUserId: Long): Boolean {
        val list = replacingWorkersUserIdsGetter()
        return list.contains(workerUserId)
    }

    fun setIsEnabledReplacement(isEnabled: Boolean) {
        this.isEnabledReplacement = isEnabled
    }

    fun refreshItemWithWorkerUserId(workerUserId: Long) {
        val index =
            items.orEmpty().indexOfFirst { it is WorkerUi && it.user.userId == workerUserId }
        notifyItemChanged(index, Payload(workerUserId))
    }

    class Payload(val workerUserId: Long)
}

class WorkerMainInfoWrapper(
    val userId: Long,
    val workerId: Long?,
    val replacingWorkerId: Long? = null
) : BaseListItem {

    override val id: String = userId.toString()

    val isNew = workerId == null
}
