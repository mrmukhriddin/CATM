package ru.metasharks.catm.feature.profile.ui.profile.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.profile.ui.databinding.ItemDataTextBinding
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListTextData

class DelegateTextData : BaseListItemDelegate<ListTextData, DelegateTextData.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDataTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(item: ListTextData, holder: ViewHolder, payloads: List<Any?>) {
        holder.bind(item)
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is ListTextData

    class ViewHolder(val binding: ItemDataTextBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListTextData) {
            with(binding) {
                title.text = item.title
            }
        }
    }
}
