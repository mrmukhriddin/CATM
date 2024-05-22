package ru.metasharks.catm.feature.documents.ui.recycler.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class DocumentDirectoryUI(
    val typeId: Int,
    val name: String,
    var nextPageUrl: String? = null,
    var listDocuments: List<BaseListItem>? = null,
    var previousDirectoryUI: DocumentDirectoryUI? = null,
) : BaseListItem {

    override val id: String
        get() = typeId.toString()
}
