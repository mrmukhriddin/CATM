package ru.metasharks.catm.feature.workpermit.ui.entities

import ru.metasharks.catm.core.ui.recycler.BaseListItem

data class DocumentUi(
    val fileName: String,
    val fileSizeBytes: Int,
    val fileUrl: String,
    val fileId: Long,
) : BaseListItem {

    override val id: String
        get() = fileId.toString()
}
