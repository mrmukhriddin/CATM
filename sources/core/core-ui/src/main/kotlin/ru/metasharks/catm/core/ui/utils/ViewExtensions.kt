package ru.metasharks.catm.core.ui.utils

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.ui.R
import ru.metasharks.catm.core.ui.snackbar.CustomSnackbar
import java.util.concurrent.TimeUnit

private const val DEBOUNCE_TIME_MS = 300L

fun ViewBinding.showSnack(text: String, length: Int) =
    CustomSnackbar.make(root, text, length).show()

fun ViewBinding.showSnack(@StringRes res: Int, length: Int) =
    CustomSnackbar.make(root, res, length).show()

fun ViewBinding.showShortSnack(@StringRes res: Int) =
    showSnack(res, Snackbar.LENGTH_SHORT)

fun ViewBinding.showShortSnack(text: String) =
    showSnack(text, Snackbar.LENGTH_SHORT)

fun ViewBinding.showLongSnack(@StringRes res: Int) =
    showSnack(res, Snackbar.LENGTH_LONG)

fun ViewBinding.showLongSnack(text: String) =
    showSnack(text, Snackbar.LENGTH_LONG)

fun ViewBinding.showIndefiniteSnack(@StringRes res: Int) =
    showSnack(res, Snackbar.LENGTH_INDEFINITE)

fun ViewBinding.showIndefiniteSnack(text: String) =
    showSnack(text, Snackbar.LENGTH_INDEFINITE)

val View.measuredHeightWithMargins: Int
    get() {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        return measuredHeight + params.topMargin + params.bottomMargin
    }

val View.measuredWidthWithMargins: Int
    get() {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        return measuredWidth + params.rightMargin + params.leftMargin
    }

val TextInputLayout.textString: String?
    get() = editText?.text?.toString()

val TextInputLayout.textStringOrEmpty: String
    get() = textString ?: ""

fun TextView.setTextOrHideField(text: String?) {
    if (text.isNullOrEmpty()) {
        isGone = true
    } else {
        isVisible = true
        this.text = text
    }
}

val RecyclerView.linearLayoutManager
    get() = layoutManager as LinearLayoutManager

fun View.hideAndShow(
    viewToShow: View,
    viewHiddenAction: ((View) -> Unit)? = null,
) {
    alpha = 1f
    animate()
        .alpha(0f)
        .withEndAction {
            isGone = true
            viewHiddenAction?.invoke(this)
            viewToShow.isVisible = true
            viewToShow.alpha = 0f
            viewToShow.animate()
                .alpha(1f)
                .start()
        }.start()
}

fun EditText.setupSearch(): Observable<String> {
    return textChanges()
        .map { it.toString() }
        .debounce(DEBOUNCE_TIME_MS, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun CompoundButton.setCustomChecked(
    value: Boolean,
    listener: CompoundButton.OnCheckedChangeListener
) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}

fun View.changeBackgroundStrokeColor(@ColorRes res: Int) {
    val back = background as? GradientDrawable ?: return
    back.mutate()
    back.setStroke(
        context.resources.getDimension(R.dimen.default_stroke_width).toInt() + 1,
        ContextCompat.getColor(context, res)
    )
}
