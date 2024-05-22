package ru.metasharks.catm.feature.profile.ui.profile.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.profile.ui.databinding.ItemDataFileBinding
import ru.metasharks.catm.feature.profile.ui.profile.entities.ListFileData

class DelegateFileData(
    private val onFileClickListener: (fileData: ListFileData) -> Unit,
) : BaseListItemDelegate<ListFileData, DelegateFileData.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDataFileBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(item: ListFileData, holder: ViewHolder, payloads: List<Any?>) {
        holder.bind(item)
        holder.binding.fileBtn.setOnClickListener {
            onFileClickListener(item)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is ListFileData

    class ViewHolder(val binding: ItemDataFileBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListFileData) {
            with(binding) {
                title.text = item.title
            }
        }
    }
}
