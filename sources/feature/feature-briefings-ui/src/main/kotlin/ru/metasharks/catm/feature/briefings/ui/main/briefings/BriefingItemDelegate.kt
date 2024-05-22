package ru.metasharks.catm.feature.briefings.ui.main.briefings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.chips.ChipUtils
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.briefings.ui.databinding.ItemBriefingCategoryBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingUi

class BriefingItemDelegate(
    private val onClick: (BriefingUi) -> Unit
) :
    BaseListItemDelegate<BriefingUi, BriefingItemDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemBriefingCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        item: BriefingUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            briefingCategoryValue.text = item.text
            root.setOnClickListener { onClick(item) }
            if (item.statusChip != null) {
                statusChip.isVisible = true
                ChipUtils.setChip(statusChip, item.statusChip)
            } else {
                statusChip.isGone = true
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is BriefingUi

    class ViewHolder(val binding: ItemBriefingCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
