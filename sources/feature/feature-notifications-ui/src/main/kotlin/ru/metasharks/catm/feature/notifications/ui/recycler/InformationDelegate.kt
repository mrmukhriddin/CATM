package ru.metasharks.catm.feature.notifications.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.notifications.ui.databinding.ItemInformationBinding
import ru.metasharks.catm.feature.notifications.ui.recycler.NotificationsAdapter.Companion.EXTEND_DELETE_ZONE_DP
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.InformationItemUi
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.extendClickableZone
import ru.metasharks.catm.utils.layoutInflater

internal class InformationDelegate(
    private val onDismiss: OnDismiss
) : BaseListItemDelegate<InformationItemUi, InformationDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemInformationBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: InformationItemUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            date.text = item.createdDate
            notificationTitle.text = item.message
            dismissNotificationBtn.extendClickableZone(root.context.dpToPx(EXTEND_DELETE_ZONE_DP))
            dismissNotificationBtn.setOnClickListener { onDismiss(item.notificationId) }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is InformationItemUi

    class ViewHolder(val binding: ItemInformationBinding) : RecyclerView.ViewHolder(binding.root)
}
