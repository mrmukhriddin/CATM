package ru.metasharks.catm.feature.briefings.ui.main.types

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.chips.ChipUtils
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.briefings.ui.databinding.ItemBriefingCategoryBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingTypeUi

class BriefingTypeDelegate(
    private val onClick: (BriefingTypeUi) -> Unit
) :
    BaseListItemDelegate<BriefingTypeUi, BriefingTypeDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemBriefingCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        item: BriefingTypeUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            briefingCategoryValue.text = item.text
            root.setOnClickListener { onClick(item) }
            if (item.chipItem == null) {
                statusChip.isGone = true
            } else {
                statusChip.isVisible = true
                ChipUtils.setChip(statusChip, item.chipItem)
            }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is BriefingTypeUi

    class ViewHolder(val binding: ItemBriefingCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
