package ru.metasharks.catm.feature.profile.ui.profile.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.profile.ui.databinding.ItemDataSignedBinding
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListSignedData

class DelegateSignedData : BaseListItemDelegate<ListSignedData, DelegateSignedData.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDataSignedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(item: ListSignedData, holder: ViewHolder, payloads: List<Any?>) {
        holder.bind(item)
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is ListSignedData

    class ViewHolder(val binding: ItemDataSignedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListSignedData) {
            with(binding) {
                title.text = item.title
                signed.isEnabled = item.signed
            }
        }
    }
}
