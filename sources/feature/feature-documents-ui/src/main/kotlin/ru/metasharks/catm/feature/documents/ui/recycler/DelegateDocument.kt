package ru.metasharks.catm.feature.documents.ui.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.databinding.LayoutDocumentBinding
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentUI
import ru.metasharks.catm.utils.strings.getFileTypeAndSizeString
import ru.metasharks.catm.utils.layoutInflater

class DelegateDocument(
    private val onDocumentClick: (DocumentUI) -> Unit
) : BaseListItemDelegate<DocumentUI, DelegateDocument.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutDocumentBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(
        item: DocumentUI,
        holder: ViewHolder,
        payloads: List<Any?>
    ) {
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onDocumentClick(item)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is DocumentUI

    class ViewHolder(val binding: LayoutDocumentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DocumentUI) {
            with(binding) {
                fileName.text = item.fileName
                fileTypeAndSize.text = getFileTypeAndSizeString(item.fileUri, item.sizeInBytes)
            }
        }
    }
}
