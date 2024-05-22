package ru.metasharks.catm.feature.documents.ui.recycler.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class DocumentUI(
    val fileId: Int,
    val fileName: String,
    val fileUri: String,
    val sizeInBytes: Int,
) : BaseListItem {

    override val id: String
        get() = fileId.toString()
}
