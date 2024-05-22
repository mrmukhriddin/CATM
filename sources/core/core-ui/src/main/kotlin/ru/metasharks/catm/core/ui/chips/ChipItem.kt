package ru.metasharks.catm.core.ui.chips

import androidx.annotation.ColorInt

class ChipItem(
    val text: String,

    @ColorInt
    val textColor: Int,

    @ColorInt
    val backgroundColor: Int,

    @ColorInt
    val strokeColor: Int? = null
)
