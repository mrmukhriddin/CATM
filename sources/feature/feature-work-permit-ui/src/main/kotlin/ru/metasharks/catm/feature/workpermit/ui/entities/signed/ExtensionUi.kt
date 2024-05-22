package ru.metasharks.catm.feature.workpermit.ui.entities.signed

data class ExtensionUi(
    val id: Long,
    val dateEnd: String,
    val isAwaiting: Boolean, // false - is already signed
    val isSigner: Boolean,
)
