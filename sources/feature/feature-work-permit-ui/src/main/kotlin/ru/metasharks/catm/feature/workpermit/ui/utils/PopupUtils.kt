package ru.metasharks.catm.feature.workpermit.ui.utils

import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import ru.metasharks.catm.feature.workpermit.ui.R
import ru.metasharks.catm.utils.layoutInflater

object PopupUtils {

    fun showPopup(anchorView: View, @StringRes messageRes: Int): PopupWindow {
        val context = anchorView.context
        return showPopup(anchorView, context.getString(messageRes))
    }

    fun showPopup(anchorView: View, notificationMessage: String): PopupWindow {
        val context = anchorView.context
        val layoutInflater = context.layoutInflater

        val textViewWrapper =
            layoutInflater.inflate(R.layout.popup_message, null, false) as FrameLayout
        val textView = textViewWrapper.children.first() as TextView
        textView.text = notificationMessage
        val popupWindow = PopupWindow(context).apply {
            contentView = textViewWrapper
            setTouchInterceptor { v, event ->
                dismiss()
                return@setTouchInterceptor true
            }
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    ru.metasharks.catm.core.ui.R.drawable.bubble_drawable
                )
            )
        }

        textViewWrapper.measure(
            View.MeasureSpec.makeMeasureSpec(popupWindow.width, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        popupWindow.height = textViewWrapper.measuredHeight
        popupWindow.width = textViewWrapper.measuredWidth
        val targetHeight = textViewWrapper.measuredHeight
        popupWindow.showAsDropDown(
            anchorView,
            anchorView.width / 2,
            -targetHeight - anchorView.height,
        )
        return popupWindow
    }
}
