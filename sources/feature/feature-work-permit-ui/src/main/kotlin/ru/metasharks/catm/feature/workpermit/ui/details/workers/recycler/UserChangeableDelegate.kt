package ru.metasharks.catm.feature.workpermit.ui.details.workers.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.RecyclerUtils
import ru.metasharks.catm.core.ui.utils.getFullName
import ru.metasharks.catm.core.ui.utils.getFullNameWithLineBreak
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemUserChangeableBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.WorkerUi
import ru.metasharks.catm.utils.layoutInflater

class UserChangeableDelegate(
    private val onWorkerClicked: OnWorkerClicked,
    private val isToReplace: (workerId: Long) -> Boolean,
    private val isEnabledReplacement: () -> Boolean,
) : BaseListItemDelegate<WorkerUi, UserChangeableDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemUserChangeableBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: WorkerUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        val user = item.user
        with(holder.binding) {
            val payload = payloads.firstOrNull()
            if (payload != null && payload is UserAdapter.Payload) {
                applyTransparency(item)
                return
            }
            applyTransparency(item)
            with(profileInfo) {
                profileName.text = getFullNameWithLineBreak(user.name, user.surname)
                if (user.position == null) {
                    profilePosition.isGone = true
                    profilePosition.text = ""
                } else {
                    profilePosition.isVisible = true
                    profilePosition.text = user.position
                }
                profileImageContainer
                    .setData(getFullName(user.name, user.surname), user.avatar)
                    .load()
            }
            isReadyIndicator.isEnabled = user.isReady ?: false
        }
    }

    fun ItemUserChangeableBinding.applyTransparency(item: WorkerUi) {
        val enabled = isToReplace(item.user.userId).not() && item.isReplaced.not()
        RecyclerUtils.makeTransparent(root, opaque = enabled)
        if (enabled) {
            changeBtn.isEnabled = isEnabledReplacement()
            changeBtn.setOnClickListener {
                onWorkerClicked(
                    WorkerMainInfoWrapper(
                        userId = item.user.userId,
                        workerId = item.workerId,
                    )
                )
            }
        } else {
            changeBtn.isEnabled = false
            changeBtn.setOnClickListener(null)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkerUi

    class ViewHolder(val binding: ItemUserChangeableBinding) : RecyclerView.ViewHolder(binding.root)
}
