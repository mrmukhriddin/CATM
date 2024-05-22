package ru.metasharks.catm.feature.workpermit.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.chips.ChipUtils
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemWorkPermitBinding

typealias OnWorkPermitClick = (WorkPermitUi) -> Unit

class WorkPermitsDelegate(
    private val onWorkPermitClick: OnWorkPermitClick
) :
    BaseListItemDelegate<WorkPermitUi, WorkPermitsDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemWorkPermitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        item: WorkPermitUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        val context = holder.itemView.context
        holder.binding.apply {
            root.setOnClickListener { onWorkPermitClick(item) }
            wpCreatedText.text =
                context.getString(R.string.wp_item_created, item.createdDate.toString(DATE_FORMAT))
            if (item.extendDate == null) {
                wpExtendedText.isGone = true
                wpExtendedText.text = ""
            } else {
                wpExtendedText.isVisible = true
                wpExtendedText.text =
                    context.getString(
                        R.string.wp_item_extended,
                        item.extendDate.toString(DATE_FORMAT)
                    )
            }
            // tags:
            wpTagsChipGroup.removeAllViews()
            for (tag in item.chips) {
                ChipUtils.addChip(wpTagsChipGroup, tag)
            }
            wpWorkTypeChipGroup.removeAllViews()
            ChipUtils.addChip(wpWorkTypeChipGroup, item.workTypeChip)
            // id
            wpIdText.text = context.getString(R.string.wp_item_work_permit_number, item.permitId)
            // address
            wpAddressText.text = item.address
            // workers
            wpWorkersCountText.text =
                context.getString(R.string.wp_item_workers_count, item.workersCount)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is WorkPermitUi

    class ViewHolder(val binding: ItemWorkPermitBinding) : RecyclerView.ViewHolder(binding.root)

    private companion object {
        const val DATE_FORMAT = "dd.MM.yyyy"
    }
}
