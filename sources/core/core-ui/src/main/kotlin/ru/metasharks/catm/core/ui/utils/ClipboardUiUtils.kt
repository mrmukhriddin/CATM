package ru.metasharks.catm.core.ui.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import ru.metasharks.catm.utils.clipboard.ClipboardHelper

object ClipboardUiUtils {

    val DEFAULT_CALLBACK: (View) -> Unit = {
        CustomSnackbar.make(it, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show()
    }

    fun copyPhoneNumber(view: View, phoneNumber: String) {
        ClipboardHelper.copyPhoneNumber(view, phoneNumber, DEFAULT_CALLBACK)
    }
}
