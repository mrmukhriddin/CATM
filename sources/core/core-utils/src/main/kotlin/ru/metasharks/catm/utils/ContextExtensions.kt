package ru.metasharks.catm.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import ru.metasharks.catm.utils.strings.StringResWrapper

fun Context.spToPx(sp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, this.resources.displayMetrics)

fun Context.dpToPx(dp: Int): Int =
    dp * resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

fun Context.dpToPx(dp: Float): Int =
    (dp * resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT).toInt()

fun Context.startApplicationDetailsActivity() {
    val uri = Uri.fromParts("package", packageName, null)
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
    )
}

fun Context.getString(stringResWrapper: StringResWrapper): String {
    return if (stringResWrapper.isResource) {
        getString(stringResWrapper.resId)
    } else {
        stringResWrapper.string
    }
}

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

fun Activity.getRootView(): View {
    return findViewById(android.R.id.content)
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = dpToPx(MARGIN_OF_ERROR_DP)
    return heightDiff > marginOfError
}

const val MARGIN_OF_ERROR_DP = 36
