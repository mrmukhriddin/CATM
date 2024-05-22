package ru.metasharks.catm.utils

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setDefinitePadding(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun View.setDefiniteMargin(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    if (layoutParams !is ViewGroup.MarginLayoutParams) {
        return
    }
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.setMargins(left, top, right, bottom)
}

fun View.extendClickableZone(paddingDp: Int) =
    extendClickableZone(paddingDp, paddingDp, paddingDp, paddingDp)

fun View.extendClickableZone(
    leftPaddingDp: Int,
    topPaddingDp: Int,
    rightPaddingDp: Int,
    bottomPaddingDp: Int,
) {
    check(parent is View) {
        "extendClickableZone() is only used for inner views"
    }
    val parentView = parent as View
    parentView.post {
        val rect = Rect()
        getHitRect(rect)
        rect.left -= context.dpToPx(leftPaddingDp)
        rect.top -= context.dpToPx(topPaddingDp)
        rect.right += context.dpToPx(rightPaddingDp)
        rect.bottom += context.dpToPx(bottomPaddingDp)
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}
