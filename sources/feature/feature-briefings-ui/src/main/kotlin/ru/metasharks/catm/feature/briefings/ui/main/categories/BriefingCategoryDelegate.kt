package ru.metasharks.catm.feature.briefings.ui.main.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.briefings.ui.databinding.ItemBriefingCategoryBinding
import ru.metasharks.catm.feature.briefings.ui.entities.BriefingCategoryUi

class BriefingCategoryDelegate(
    private val onClick: (BriefingCategoryUi) -> Unit
) :
    BaseListItemDelegate<BriefingCategoryUi, BriefingCategoryDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemBriefingCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        item: BriefingCategoryUi,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        with(holder.binding) {
            briefingCategoryValue.text = item.text
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is BriefingCategoryUi

    class ViewHolder(val binding: ItemBriefingCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
