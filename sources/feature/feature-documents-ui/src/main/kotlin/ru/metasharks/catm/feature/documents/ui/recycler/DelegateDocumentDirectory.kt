package ru.metasharks.catm.feature.documents.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.documents.ui.databinding.ItemDocumentTypeBinding
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentDirectoryUI
import ru.metasharks.catm.utils.layoutInflater

class DelegateDocumentDirectory(
    private val onDirectoryClick: (DocumentDirectoryUI) -> Unit
) : BaseListItemDelegate<DocumentDirectoryUI, DelegateDocumentDirectory.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemDocumentTypeBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: DocumentDirectoryUI,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onDirectoryClick(item)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is DocumentDirectoryUI

    class ViewHolder(val binding: ItemDocumentTypeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DocumentDirectoryUI) {
            binding.documentDirectoryName.text = item.name
        }
    }
}
