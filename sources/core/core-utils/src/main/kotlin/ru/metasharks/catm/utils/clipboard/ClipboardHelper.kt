package ru.metasharks.catm.utils.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.view.View

object ClipboardHelper {

    fun copyPhoneNumber(view: View, phoneNumber: String, onCopiedCallback: ((View) -> Unit)? = null) {
        val clipboard = view.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            LABEL_PHONE_NUMBER,
            phoneNumber
        )
        clipboard.setPrimaryClip(clipData)
        onCopiedCallback?.let { it(view) }
    }

    private const val LABEL_PHONE_NUMBER = "phone_number"
}
