package ru.metasharks.catm.core.ui.bottom

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.github.terrakok.cicerone.Screen

class CoreScreenPage constructor(
    val info: PageInfo,
    val screenFactory: () -> Screen,
    val args: Bundle? = null
)

data class PageInfo(
    @IdRes val menuId: Int,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
)
