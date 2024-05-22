package ru.metasharks.catm.feature.documents.ui.recycler

import ru.metasharks.catm.core.ui.recycler.empty.search.EmptySearchResultDelegate
import ru.metasharks.catm.core.ui.recycler.pagination.OnNearTheEndListener
import ru.metasharks.catm.core.ui.recycler.pagination.PaginationListDelegationAdapter
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentDirectoryUI
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentUI

class DocumentsAdapter(
    onDirectoryClick: (DocumentDirectoryUI) -> Unit,
    onDocumentClick: (DocumentUI) -> Unit,
    onNearTheEndListener: OnNearTheEndListener,
) : PaginationListDelegationAdapter(onNearTheEndListener) {

    init {
        items = emptyList()
        delegatesManager
            .addDelegate(DelegateDocumentDirectory(onDirectoryClick))
            .addDelegate(DelegateDocument(onDocumentClick))
            .addDelegate(EmptySearchResultDelegate)
    }
}
