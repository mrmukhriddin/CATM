package ru.metasharks.catm.feature.workpermit.ui.details.adddocs.recycler

import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.workpermit.ui.entities.DocumentUi

class AdditionalDocumentsAdapter(
    onDocumentClick: OnDocumentClick,
    onDeleteClick: OnDocumentClick,
) : PaginationListDelegationAdapter(null) {

    private var isDeletionAvailable = false

    private val oldList
        get() = items ?: emptyList()

    fun addItem(loadedFile: DocumentUi) {
        setItems(oldList.plus(loadedFile))
    }

    fun deleteItem(file: DocumentUi) {
        val newList = oldList.toMutableList()
        val indexOfDeletedItem = newList.indexOfFirst { it.id == file.id }
        newList.removeAt(indexOfDeletedItem)
        setItems(newList)
    }

    fun setDeleteAvailable(available: Boolean) {
        isDeletionAvailable = available
    }

    init {
        delegatesManager.addDelegate(
            AdditionalDocumentDelegate(
                onDocumentClick,
                onDeleteClick
            ) { isDeletionAvailable }
        )
    }
}
