package ru.metasharks.catm.core.ui.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.metasharks.catm.core.ui.R

@ColorInt
fun Context.getColorForChar(char: Char?): Int =
    ContextCompat.getColor(
        this, when {
            char == null -> R.color.yellow
            char <= 'Д' -> R.color.green
            char <= 'И' -> R.color.red
            char <= 'О' -> R.color.orange
            char <= 'У' -> R.color.blue
            char <= 'Ш' -> R.color.purple
            char <= 'Э' -> R.color.cyan
            else -> R.color.yellow
        }
    )
