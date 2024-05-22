package ru.metasharks.catm.core.ui.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

object RecyclerUtils {

    fun makeTransparent(root: ViewGroup, opaque: Boolean, vararg additionalViews: View) {
        val newAlpha = if (opaque) {
            OPAQUE_PERCENT
        } else {
            TRANSPARENT_PERCENT
        }
        root.background.alpha = (FULL_OPAQUE * newAlpha).toInt()
        root.children.forEach { it.alpha = newAlpha }
        additionalViews.forEach { it.alpha = newAlpha }
    }

    private const val FULL_OPAQUE = 255
    private const val OPAQUE_PERCENT = 1.0f
    private const val TRANSPARENT_PERCENT = .5f
}
