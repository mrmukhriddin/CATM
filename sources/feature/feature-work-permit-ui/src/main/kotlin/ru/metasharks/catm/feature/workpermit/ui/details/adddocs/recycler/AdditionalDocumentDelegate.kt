package ru.metasharks.catm.feature.workpermit.ui.details.adddocs.recycler

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.metasharks.catm.core.ui.recycler.BaseListItem
import ru.metasharks.catm.core.ui.recycler.BaseListItemDelegate
import ru.metasharks.catm.feature.workpermit.ui.databinding.ItemAdditionalDocBinding
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi
import ru.metasharks.catm.utils.layoutInflater
import ru.metasharks.catm.utils.strings.getSizeString

typealias OnDocumentClick = (DocumentUi) -> Unit

class AdditionalDocumentDelegate(
    private val onDocumentClick: OnDocumentClick,
    private val onDeleteClick: OnDocumentClick,
    private val isDeletionAvailable: () -> Boolean
) : BaseListItemDelegate<DocumentUi, AdditionalDocumentDelegate.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            ItemAdditionalDocBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(item: DocumentUi, holder: ViewHolder, payloads: List<Any?>) {
        with(holder.binding) {
            root.setOnClickListener { onDocumentClick(item) }
            if (isDeletionAvailable()) {
                deleteFileBtn.isVisible = true
                deleteFileBtn.setOnClickListener { onDeleteClick(item) }
            } else {
                deleteFileBtn.isGone = true
                deleteFileBtn.setOnClickListener(null)
            }
            fileNameLabel.text = item.fileName
            fileSizeLabel.text = getSizeString(item.fileSizeBytes)
        }
    }

    override fun isForViewType(item: BaseListItem): Boolean = item is DocumentUi

    class ViewHolder(val binding: ItemAdditionalDocBinding) : RecyclerView.ViewHolder(binding.root)
}
