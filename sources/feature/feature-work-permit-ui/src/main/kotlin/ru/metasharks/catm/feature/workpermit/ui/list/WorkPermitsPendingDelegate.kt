package ru.metasharks.catm.feature.workpermit.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemWorkPermitBinding

class WorkPermitsPendingDelegate :
    BaseListItemDelegate<WorkPermitPendingUi, WorkPermitsPendingDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemWorkPermitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        item: WorkPermitPendingUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        val context = holder.itemView.context
        holder.binding.apply {
            wpCreatedText.setText(R.string.wp_awaiting_internet_connection)
            wpExtendedText.isGone = true
            // tags:
            wpTagsChipGroup.removeAllViews()
            wpTagsChipGroup.isGone = true
            wpWorkTypeChipGroup.removeAllViews()
            wpWorkTypeChipGroup.isGone = true
            // id
            wpIdText.text = context.getString(R.string.wp_item_work_permit_no_number)
            // address
            wpAddressText.text = item.address
            // workers
            wpWorkersCountText.text =
                context.getString(R.string.wp_item_workers_count, item.workersCount)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkPermitPendingUi

    class ViewHolder(val binding: ItemWorkPermitBinding) : RecyclerView.ViewHolder(binding.root)
}
