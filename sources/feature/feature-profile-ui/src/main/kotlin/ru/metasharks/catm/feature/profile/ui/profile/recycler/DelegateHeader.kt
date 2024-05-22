package ru.metasharks.catm.feature.profile.ui.profile.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.profile.ui.databinding.ItemHeaderBinding
import ru.metasharks.catm.feature.profile.ui.profile.entities.Header

class DelegateHeader : BaseListItemDelegate<Header, DelegateHeader.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(item: Header, holder: ViewHolder, payloads: List<Any?>) {
        holder.bind(item)
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is Header

    class ViewHolder(val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Header) {
            with(binding) {
                root.text = item.id
            }
        }
    }
}
