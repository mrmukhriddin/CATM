package ru.metasharks.catm.feature.workpermit.ui.details.dailypermits.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.core.ui.utils.getHighlightedString
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemDailyPermitBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.signed.DailyPermitPendingUi
import ru.metasharks.catm.utils.layoutInflater

class DailyPermitPendingDelegate :
    BaseListItemDelegate<DailyPermitPendingUi, DailyPermitPendingDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDailyPermitBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: DailyPermitPendingUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        val context = holder.binding.root.context
        with(holder.binding) {
            dpDate.setText(R.string.waiting_for_connection)
            dpTime.isGone = true
            dpResponsible.isGone = true
            dpPermitter.text = getHighlightedString(
                context, R.string.dp_permitter, item.permitterSignerName
            )
            setVisibility()
        }
    }

    private fun ItemDailyPermitBinding.setVisibility(
        primaryVisible: Boolean = false,
        secondaryVisible: Boolean = false,
        statusDisplayerVisible: Boolean = false
    ) {
        primaryButton.isVisible = primaryVisible
        secondaryButton.isVisible = secondaryVisible
        statusDisplayer.isVisible = statusDisplayerVisible
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is DailyPermitPendingUi

    class ViewHolder(val binding: ItemDailyPermitBinding) : RecyclerView.ViewHolder(binding.root)
}
