package ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemUserChangeableBinding
import ru.metasharks.catm.feature.workpermit.ui.details.workers.dialog.WorkerListItemUi
import ru.metasharks.catm.utils.layoutInflater

class UserAddedDelegate(
    val onWorkerClicked: OnWorkerClicked,
) : BaseListItemDelegate<WorkerListItemUi, UserAddedDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemUserChangeableBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: WorkerListItemUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            with(profileInfo) {
                profileName.text = getFullNameWithLineBreak(item.name, item.surname)
                if (item.position == null) {
                    profilePosition.isGone = true
                    profilePosition.text = ""
                } else {
                    profilePosition.isVisible = true
                    profilePosition.text = item.position
                }
                profileImageContainer.setData(getFullName(item.name, item.surname), item.avatar)
                    .load()
            }
            changeBtn.setOnClickListener {
                onWorkerClicked(
                    WorkerMainInfoWrapper(
                        userId = item.userId,
                        workerId = null,
                        replacingWorkerId = item.replacingWorkerId
                    ),
                )
            }
            isReadyIndicator.isEnabled = item.isReady
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkerListItemUi

    class ViewHolder(val binding: ItemUserChangeableBinding) : RecyclerView.ViewHolder(binding.root)
}
