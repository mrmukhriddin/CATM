package ru.metasharks.catm.feature.documents.ui.recycler.entities.mapper

import ru.metasharks.catm.feature.documents.entities.DocumentTypeX
import ru.metasharks.catm.feature.documents.entities.DocumentX
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentDirectoryUI
import ru.metasharks.catm.feature.documents.ui.recycler.entities.DocumentUI
import javax.inject.Inject

internal class Mapper @Inject constructor() {

    fun mapDocumentType(documentTypeX: DocumentTypeX): DocumentDirectoryUI {
        return DocumentDirectoryUI(
            typeId = documentTypeX.id,
            name = documentTypeX.name
        )
    }

    fun mapDocument(documentX: DocumentX): DocumentUI {
        return DocumentUI(
            fileId = documentX.id,
            fileUri = documentX.fileUri,
            fileName = documentX.title,
            sizeInBytes = documentX.fileSize
        )
    }
}
