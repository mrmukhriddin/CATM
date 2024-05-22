package ru.metasharks.catm.feature.notifications.ui.recycler

import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.chips.ChipItem
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.notifications.ui.databinding.ItemButtonActionBinding
import ru.metasharks.catm.feature.notifications.ui.recycler.NotificationsAdapter.Companion.EXTEND_DELETE_ZONE_DP
import ru.metasharks.catm.feature.notifications.ui.recycler.entities.ButtonActionItemUi
import ru.metasharks.catm.utils.dpToPx
import ru.metasharks.catm.utils.extendClickableZone
import ru.metasharks.catm.utils.layoutInflater

internal typealias OnButtonActionClick = (ButtonActionItemUi) -> Unit

internal class ButtonActionDelegate(
    private val onSignWorkPermitClick: OnButtonActionClick,
    private val onDismiss: OnDismiss,
) : BaseListItemDelegate<ButtonActionItemUi, ButtonActionDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemButtonActionBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: ButtonActionItemUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            date.text = item.createdDate
            actionBtn.text = item.payload.buttonText
            actionBtn.setOnClickListener { onSignWorkPermitClick(item) }
            dismissNotificationBtn.setOnClickListener { onDismiss(item.notificationId) }
            notificationTitle.text = item.message
            dismissNotificationBtn.extendClickableZone(root.context.dpToPx(EXTEND_DELETE_ZONE_DP))
            notificationMessage.isGone = true // later find out what needed to be hardcoded
            actionBtn.setOnClickListener {
                onSignWorkPermitClick(item)
            }
            typeChip.setChip(item.payload.chipItem)
        }
    }

    private fun TextView.setChip(chipItem: ChipItem) {
        val backgroundDrawable = GradientDrawable()
        with(backgroundDrawable) {
            cornerRadius = context.dpToPx(CHIP_CORNER_RADIUS_DP).toFloat()
            chipItem.strokeColor?.let {
                setStroke(context.dpToPx(1), it)
            }
            setColor(chipItem.backgroundColor)
        }
        background = backgroundDrawable
        setTextColor(chipItem.textColor)
        text = chipItem.text
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is ButtonActionItemUi

    class ViewHolder(val binding: ItemButtonActionBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        private const val CHIP_CORNER_RADIUS_DP = 4
    }
}
